package uk.joshiejack.simplyseasons.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
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
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class SSServerWorld extends World {
    protected SSServerWorld(ISpawnWorldInfo info, RegistryKey<World> key, DimensionType type,
                            Supplier<IProfiler> profiler, boolean clientSide, boolean debug, long biomeZoomSeed) {
        super(info, key, type, profiler, clientSide, debug, biomeZoomSeed);
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;shouldFreeze(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z"), cancellable = true)
    public void shouldMelt(Chunk chunk, int ticks, CallbackInfo ci) {
        int i = chunk.getPos().getMinBlockX();
        int j = chunk.getPos().getMinBlockZ();
        BlockPos pos = this.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, this.getBlockRandomPos(i, 0, j, 15));
        BlockPos below = pos.below();
        float temperature = SeasonalWorlds.getTemperature(this, this.getBiome(pos), pos);
        if (temperature >= 0.15F) {
            BlockState state = getBlockState(pos);
            //Melt any snow if the temperature is above freezing point
            if (state.getBlock() == Blocks.SNOW) {
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

            if (getBlockState(below).getBlock() == Blocks.ICE)
                setBlockAndUpdate(below, Blocks.WATER.defaultBlockState());
        }
    }
}
