package uk.joshiejack.simplyseasons.mixins;

import net.minecraft.block.*;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.api.Weather;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;
import uk.joshiejack.simplyseasons.plugins.SereneSeasonsPlugin;
import uk.joshiejack.simplyseasons.world.CalendarDate;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class SSServerWorld extends World {
    private static final CalendarDate DATE = new CalendarDate();

    protected SSServerWorld(ISpawnWorldInfo info, RegistryKey<World> key, DimensionType type,
                            Supplier<IProfiler> profiler, boolean clientSide, boolean debug, long biomeZoomSeed) {
        super(info, key, type, profiler, clientSide, debug, biomeZoomSeed);
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 1), cancellable = true)
    public void shouldMeltOrGrow(Chunk chunk, int ticks, CallbackInfo ci) {
        if (BetterWeatherPlugin.loaded || SereneSeasonsPlugin.loaded) return;
        LazyOptional<ISeasonProvider> provider = getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (!provider.isPresent()) return; //If seasons are irrelevant to this world
        Season season = provider.resolve().get().getSeason(this);
        int i = chunk.getPos().getMinBlockX();
        int j = chunk.getPos().getMinBlockZ();
        BlockPos pos = this.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, this.getBlockRandomPos(i, 0, j, 15));
        BlockPos below = pos.below();
        BlockState state = getBlockState(pos);
        float temperature = SeasonalWorlds.getTemperature(this, getBiome(pos), pos);
        int monthday = CalendarDate.getDay(getDayTime());
        //////////////// MELT ICE //////////////////
        if (SeasonalWorlds.shouldMelt(getBlockState(below), temperature, Blocks.ICE))
            setBlockAndUpdate(below, Blocks.WATER.defaultBlockState());
        //////////////// MELT SNOW ////////////////
        for (int t = 0; t < (season == Season.SUMMER ? 4 : 1); t++) {
            int chance = season == Season.SPRING ? (monthday < 3 ? 12 : monthday < 7 ? 6 : monthday < 14 ? 3 : 1) : 1;
            if (random.nextInt(chance) == 0) {
                if (SeasonalWorlds.shouldMelt(getBlockState(pos), temperature, Blocks.SNOW)) {
                    if (state.getValue(SnowBlock.LAYERS) > 1) {
                        setBlockAndUpdate(pos, state.setValue(SnowBlock.LAYERS, state.getValue(SnowBlock.LAYERS) - 1));
                    } else {
                        setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        if (season == Season.SPRING) {
                            BlockState belowState = getBlockState(below);
                            if (random.nextFloat() <= 0.05F && belowState.getBlock() instanceof GrassBlock) {
                                IGrowable igrowable = (IGrowable) belowState.getBlock();
                                if (igrowable.isValidBonemealTarget(this, below, belowState, false))
                                    igrowable.performBonemeal((ServerWorld) (Object) this, random, below, belowState);
                            } else if (random.nextFloat() <= 0.2F && belowState.getBlock() == Blocks.DIRT)
                                setBlockAndUpdate(below, getBiome(below).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial());
                        }
                    }
                }
            }
        }

        //////////////// SPAWN SNOW IN BLIZZARD ////////////////
        if (random.nextInt(4) == 0 && SeasonalWorlds.getTemperature(this, getBiome(pos), pos) < 0.15F &&
                pos.getY() >= 0 && pos.getY() < 256 && this.getBrightness(LightType.BLOCK, pos) < 10) {
            getCapability(SSeasonsAPI.WEATHER_CAPABILITY).ifPresent(weatherProvider -> {
                if (weatherProvider.getWeather(this) == Weather.STORM) {
                    if (state.getBlock() == Blocks.SNOW && state.getValue(SnowBlock.LAYERS) != 8)
                        setBlockAndUpdate(pos, state.setValue(SnowBlock.LAYERS, state.getValue(SnowBlock.LAYERS) + 1));
                    else if (getBiome(pos).shouldSnow(this, pos)) //Add snow at twice the pace in blizzards
                        setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
                }
            });
        }
    }
}
