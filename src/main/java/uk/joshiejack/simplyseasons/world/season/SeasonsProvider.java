package uk.joshiejack.simplyseasons.world.season;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.CalendarDate;
import uk.joshiejack.simplyseasons.world.SSSavedData;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public class SeasonsProvider extends AbstractSeasonsProvider {
    private final EnumMap<Season, EnumSet<Season>> seasonsSets = new EnumMap<>(Season.class);
    private final Season[] seasons;
    private final int length;
    private Season season;

    public SeasonsProvider(Season... seasons) {
        this.seasons = seasons;
        this.length = seasons.length;
        this.season = seasons[0];
        for (Season season: Season.values())
            seasonsSets.put(season, EnumSet.of(season));
    }

    @Override
    public void recalculate(Level world) {
        long time = world.getDayTime();
        season = seasons[Math.max(0, (int) Math.floor(((float) TimeHelper.getElapsedDays(time) / CalendarDate.seasonLength(world)) % length))];
        super.recalculate(world); //call the super to perform updates
        if (world instanceof ServerLevel level)
            SSSavedData.get(level).setDirty();
    }

    @Override
    public Season getSeason(Level world) {
        return season;
    }

    @Override
    public void setSeason(Level world, Season season) {
        if (world.isClientSide)
            this.season = season;
        else {
            ServerLevel sWorld = (ServerLevel) world;
            if (this.season.ordinal() == season.ordinal()) return;
            long length = (long) (CalendarDate.seasonLength(world) * 24000L);
            switch (this.season) {
                case SPRING:
                    sWorld.setDayTime(sWorld.getDayTime() + length * season.ordinal());
                    break;
                case SUMMER:
                    if (season == Season.SPRING)
                        sWorld.setDayTime(sWorld.getDayTime() + length * 3);
                    else
                        sWorld.setDayTime(sWorld.getDayTime() + length * (season.ordinal() - 1));
                    break;
                case AUTUMN:
                    if (season == Season.WINTER)
                        sWorld.setDayTime(sWorld.getDayTime() + length);
                    else
                        sWorld.setDayTime(sWorld.getDayTime() + length * (season.ordinal() + 2));
                    break;
                case WINTER:
                    sWorld.setDayTime(sWorld.getDayTime() + length * (season.ordinal() + 1));
                    break;
            }

            recalculate(world);
        }
    }

    private static boolean isTag(Registry<Biome> biomeRegistry, Holder<Biome> biomeHolder, TagKey<Biome> tag) {
        return biomeRegistry.getOrCreateTag(tag).contains(biomeHolder);
    }

    private Season fromBiomeOr(Level world, Season season, Holder<Biome> biome) {
        //If we can snow, it is winter
        //Savanna, Wet season = Summer
        //Desert/Mesa, Wet season = Winter
        //Jungle, Dry season = Summer/Autumn
        Registry<Biome> biomeRegistry = world.registryAccess().registryOrThrow(Registries.BIOME);
        return isTag(biomeRegistry, biome, BiomeTags.IS_SAVANNA) ? season == Season.SUMMER ? Season.WET : Season.DRY :
                isTag(biomeRegistry, biome, Tags.Biomes.IS_SANDY) ? season == Season.WINTER ? Season.WET : Season.DRY :
                       isTag(biomeRegistry, biome, BiomeTags.IS_JUNGLE) ? season == Season.SUMMER || season == Season.AUTUMN ? Season.DRY : Season.WET :
                                season;
    }

    @Override
    public Set<Season> getSeasonsAt(Level world, BlockPos pos) {
        Holder<Biome> biome = world.getBiome(pos);
        Set<Season> seasons = EnumSet.noneOf(Season.class);
        SSeasonsAPI.LOCALIZED_SEASON_HANDLER.forEach(local ->
                seasons.addAll(local.getSeasonsAt(world, pos)));
        return seasons.isEmpty() ? seasonsSets.get(biome.value().shouldSnow(world, pos) ? Season.WINTER: fromBiomeOr(world, season, biome)) : seasons;
    }

    public void save(CompoundTag data) {
        data.putByte("Season", (byte) season.ordinal());
    }

    public void load(CompoundTag nbt) {
        season = Season.values()[nbt.getByte("Season")];
    }
}
