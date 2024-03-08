package uk.joshiejack.simplyseasons.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.neoforged.neoforge.common.Tags;

public class SurfaceRules {
    public static BlockState getSurface(NoiseBasedChunkGenerator generator, BlockPos below, ServerLevel level, Holder<Biome> biome) {
        //TODO: hack through the SurfaceRules to get the top block
        return biome.is(Tags.Biomes.IS_MUSHROOM) ? Blocks.MYCELIUM.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState();
    }
}
