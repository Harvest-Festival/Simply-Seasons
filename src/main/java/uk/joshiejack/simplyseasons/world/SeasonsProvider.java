package uk.joshiejack.simplyseasons.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import uk.joshiejack.penguinlib.util.helpers.minecraft.TimeHelper;
import uk.joshiejack.simplyseasons.SeasonsConfig;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public class SeasonsProvider implements ISeasonsProvider {
    private final EnumMap<Season, EnumSet<Season>> seasonsSets = new EnumMap<>(Season.class);
    private final Season[] seasons;
    private final int length;
    private final LazyOptional<SeasonsProvider> capability;
    private Season season;

    public SeasonsProvider(Season... seasons) {
        this.seasons = seasons;
        this.length = seasons.length;
        this.season = seasons[0];
        for (Season season: Season.values())
            seasonsSets.put(season, EnumSet.of(season));
        this.capability = LazyOptional.of(() -> this);
    }

    @Override
    public void recalculate(ServerWorld world) {
        long time = world.getDayTime();
        season = seasons[Math.max(0, (int) Math.floor(((float) TimeHelper.getElapsedDays(time) /
                (float) (SeasonsConfig.daysPerSeasonMultiplier * SimplySeasons.DAYS_PER_SEASON)) % length))];
    }

    @Override
    public Season getSeason() {
        return season;
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

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == SSeasonsAPI.SEASONS_CAPABILITY ? capability.cast() : LazyOptional.empty();
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

    //Stay down here out of my way!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public static class Storage implements Capability.IStorage<ISeasonsProvider> {
        @Override
        public void readNBT(Capability<ISeasonsProvider> capability, ISeasonsProvider instance, Direction side, INBT nbt) {
            instance.deserializeNBT((CompoundNBT) nbt);
        }

        @Nullable
        @Override
        public INBT writeNBT(Capability<ISeasonsProvider> capability, ISeasonsProvider instance, Direction side) {
            return instance.serializeNBT();
        }
    }
}
