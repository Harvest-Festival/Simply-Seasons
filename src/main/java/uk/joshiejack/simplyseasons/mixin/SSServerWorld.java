package uk.joshiejack.simplyseasons.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.joshiejack.simplyseasons.api.*;
import uk.joshiejack.simplyseasons.plugin.SereneSeasonsPlugin;
import uk.joshiejack.simplyseasons.world.CalendarDate;
import uk.joshiejack.simplyseasons.world.SurfaceRules;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mixin(value = ServerLevel.class, priority = 999)
public abstract class SSServerWorld extends Level {
    @Shadow public abstract @NotNull ServerChunkCache getChunkSource();

    protected SSServerWorld(WritableLevelData data, ResourceKey<Level> key, RegistryAccess access, Holder<DimensionType> dim, Supplier<ProfilerFiller> profiler, boolean b1, boolean b2, long l, int p) {
        super(data, key, access, dim, profiler, b1, b2, l, p);
    }

    @Redirect(method = "tickPrecipitation", at = @At(value = "INVOKE", target = "net/minecraft/world/level/biome/Biome.getPrecipitationAt (Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    public Biome.Precipitation getBiomePrecipitation(Biome biome, BlockPos pos) {
        return SeasonalWorlds.getPrecipitationAt(this, biome, pos);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "net/minecraft/util/RandomSource.nextInt (I)I", ordinal = 1))
    public void shouldMeltOrGrow(LevelChunk chunk, int ticks, CallbackInfo ci) {
        if (SereneSeasonsPlugin.loaded) return;
        Optional<ISeasonProvider> provider = SSeasonsAPI.instance().getSeasonProvider(dimension());
        if (!provider.isPresent()) return; //If seasons are irrelevant to this world
        Season season = provider.get().getSeason(this);
        int i = chunk.getPos().getMinBlockX();
        int j = chunk.getPos().getMinBlockZ();
        BlockPos pos = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.getBlockRandomPos(i, 0, j, 15));
        BlockPos below = pos.below();
        BlockState state = getBlockState(pos);
        float temperature = SeasonalWorlds.getTemperature(this, getBiome(pos), pos);
        int dayOfMonth = CalendarDate.getDay(this);
        //////////////// MELT ICE //////////////////
        if (SeasonalWorlds.shouldMelt(getBlockState(below), temperature, Blocks.ICE))
            setBlockAndUpdate(below, Blocks.WATER.defaultBlockState());
        //////////////// MELT SNOW ////////////////
        NoiseBasedChunkGenerator generator = getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator ? (NoiseBasedChunkGenerator) getChunkSource().getGenerator() : null;
        for (int t = 0; t < (season == Season.SUMMER ? 4 : 1); t++) {
            int chance = season == Season.SPRING ? (dayOfMonth < 3 ? 12 : dayOfMonth < 7 ? 6 : dayOfMonth < 14 ? 3 : 1) : 1;
            if (random.nextInt(chance) == 0) {
                if (SeasonalWorlds.shouldMelt(getBlockState(pos), temperature, Blocks.SNOW)) {
                    if (state.getValue(SnowLayerBlock.LAYERS) > 1) {
                        setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, state.getValue(SnowLayerBlock.LAYERS) - 1));
                    } else {
                        setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        if (season == Season.SPRING) {
                            BlockState belowState = getBlockState(below);
                            if (random.nextFloat() <= 0.05F && belowState.getBlock() instanceof GrassBlock
                                    && (!belowState.is(SeasonalWorlds.SPRING_NO_BONE_MEAL_BLOCKS))) {
                                BonemealableBlock bonemealableBlock = (BonemealableBlock) belowState.getBlock();
                                if (bonemealableBlock.isValidBonemealTarget(this, below, belowState)) {
                                    try {
                                        bonemealableBlock.performBonemeal((ServerLevel) (Object) this, random, below, belowState);
                                    } catch (Exception ignored) {}
                                }
                            } else if (random.nextFloat() <= 0.2F && belowState.getBlock() == Blocks.DIRT && generator != null) {
                                setBlockAndUpdate(below, SurfaceRules.getSurface(generator, below, (ServerLevel) (Object) this, getBiome(below)));
                            }
                        }
                    }
                }
            }
        }

        //////////////// SPAWN SNOW IN BLIZZARD ////////////////
        if (random.nextInt(4) == 0 && SeasonalWorlds.getTemperature(this, getBiome(pos), pos) < 0.15F &&
                pos.getY() >= 0 && pos.getY() < 256 && this.getBrightness(LightLayer.BLOCK, pos) < 10) {
            SSeasonsAPI.instance().getWeatherProvider(dimension()).ifPresent(weatherProvider -> {
                if (weatherProvider.getWeather(this) == Weather.STORM) {
                    if (state.getBlock() == Blocks.SNOW && state.getValue(SnowLayerBlock.LAYERS) != 8)
                        setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, state.getValue(SnowLayerBlock.LAYERS) + 1));
                    else if (getBiome(pos).value().shouldSnow(this, pos)) //Add snow at twice the pace in blizzards
                        setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
                }
            });
        }
    }
}
