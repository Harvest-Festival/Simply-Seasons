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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;
import uk.joshiejack.simplyseasons.plugins.SereneSeasonsPlugin;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;
import uk.joshiejack.simplyseasons.world.weather.Weather;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class SSServerWorld extends World {
    protected SSServerWorld(ISpawnWorldInfo info, RegistryKey<World> key, DimensionType type,
                            Supplier<IProfiler> profiler, boolean clientSide, boolean debug, long biomeZoomSeed) {
        super(info, key, type, profiler, clientSide, debug, biomeZoomSeed);
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;shouldFreeze(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z"), cancellable = true)
    public void shouldMeltOrGrow(Chunk chunk, int ticks, CallbackInfo ci) {
        if (BetterWeatherPlugin.loaded || SereneSeasonsPlugin.loaded) return;
        int i = chunk.getPos().getMinBlockX();
        int j = chunk.getPos().getMinBlockZ();
        BlockPos pos = this.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, this.getBlockRandomPos(i, 0, j, 15));
        BlockPos below = pos.below();
        BlockState state = getBlockState(pos);
        float temperature = SeasonalWorlds.getTemperature(this, getBiome(pos), pos);
        if (SeasonalWorlds.shouldMelt(this, below, getBlockState(below), temperature, Blocks.ICE))
            setBlockAndUpdate(below, Blocks.WATER.defaultBlockState());
        else if (SeasonalWorlds.shouldMelt(this, pos, state, temperature, Blocks.SNOW)) {
            if (state.getValue(SnowBlock.LAYERS) > 1)
                setBlockAndUpdate(pos, state.setValue(SnowBlock.LAYERS, state.getValue(SnowBlock.LAYERS) - 1));
            else {
                setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                if (random.nextFloat() <= 0.05F) { //Chance in spring for flowers/grass to grow
                    getCapability(SSeasonsAPI.SEASONS_CAPABILITY).ifPresent(provider -> {
                        if (provider.getSeason(this) == Season.SPRING) {
                            BlockState blockstate = getBlockState(below);
                            if (blockstate.getBlock() instanceof GrassBlock) {
                                IGrowable igrowable = (IGrowable) blockstate.getBlock();
                                if (igrowable.isValidBonemealTarget(this, below, blockstate, false))
                                    igrowable.performBonemeal((ServerWorld) (Object) this, random, below, blockstate);
                            }
                        }
                    });
                }
            }
        } else if (SeasonalWorlds.getTemperature(this, getBiome(pos), pos) < 0.15F && state.getBlock() == Blocks.SNOW &&
                pos.getY() >= 0 && pos.getY() < 256 && this.getBrightness(LightType.BLOCK, pos) < 10) {
            getCapability(SSeasonsAPI.WEATHER_CAPABILITY).ifPresent(provider -> {
                if (provider.getWeather(this) == Weather.STORM) {
                    if (state.getValue(SnowBlock.LAYERS) != 8)
                        setBlockAndUpdate(pos, state.setValue(SnowBlock.LAYERS, state.getValue(SnowBlock.LAYERS) + 1));
                }
            });
        }
    }
}
