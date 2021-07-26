package uk.joshiejack.simplyseasons.world.season;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.INBTSerializable;
import uk.joshiejack.penguinlib.util.helpers.TimeHelper;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.CalendarDate;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public class SeasonsProvider extends AbstractSeasonsProvider implements INBTSerializable<CompoundNBT> {
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
    public void recalculate(World world) {
        long time = world.getDayTime();
        season = seasons[Math.max(0, (int) Math.floor(((float) TimeHelper.getElapsedDays(time) / CalendarDate.seasonLength(world)) % length))];
        super.recalculate(world); //call the super to perform updates
    }

    @Override
    public Season getSeason(World world) {
        return season;
    }

    @Override
    public void setSeason(World world, Season season) {
        if (world.isClientSide)
            this.season = season;
        else {
            ServerWorld sWorld = (ServerWorld) world;
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

    private Season fromBiomeOr(World world, Season season, Biome biome) {
        //If we can snow, it is winter
        //Savanna, Wet season = Summer
        //Desert/Mesa, Wet season = Winter
        //Jungle, Dry season = Summer/Autumn
        RegistryKey<Biome> key = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(biome).get();
        return BiomeDictionary.hasType(key, BiomeDictionary.Type.SAVANNA) ? season == Season.SUMMER ? Season.WET : Season.DRY :
                BiomeDictionary.hasType(key, BiomeDictionary.Type.SANDY) ? season == Season.WINTER ? Season.WET : Season.DRY :
                        BiomeDictionary.hasType(key, BiomeDictionary.Type.JUNGLE) ? season == Season.SUMMER || season == Season.AUTUMN ? Season.DRY : Season.WET :
                                season;
    }

    @Override
    public Set<Season> getSeasonsAt(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        Set<Season> seasons = EnumSet.noneOf(Season.class);
        SSeasonsAPI.LOCALIZED_SEASON_HANDLER.forEach(local ->
                seasons.addAll(local.getSeasonsAt(world, pos)));
        return seasons.isEmpty() ? seasonsSets.get(biome.shouldSnow(world, pos) ? Season.WINTER: fromBiomeOr(world, season, biome)) : seasons;
    }

    @Nullable
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT data = new CompoundNBT();
        data.putByte("Season", (byte) season.ordinal());
        return data;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        season = Season.values()[nbt.getByte("Season")];
    }

}
