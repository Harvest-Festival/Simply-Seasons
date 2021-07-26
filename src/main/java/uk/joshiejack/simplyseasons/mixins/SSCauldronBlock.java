package uk.joshiejack.simplyseasons.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

@Mixin(value = CauldronBlock.class, priority = 999)
public class SSCauldronBlock {
    /**
     * Overwrites the cauldron handleRain method
     * so that it accounts for the current season
     * @author joshiejack
     */
    @Overwrite
    public void handleRain(World world, BlockPos pos) {
        if (world.random.nextInt(20) == 1) {
            float f = SeasonalWorlds.getTemperature(world, world.getBiome(pos), pos);
            if (!(f < 0.15F)) {
                BlockState blockstate = world.getBlockState(pos);
                if (blockstate.getValue(CauldronBlock.LEVEL) < 3) {
                    world.setBlock(pos, blockstate.cycle(CauldronBlock.LEVEL), 2);
                }
            }
        }
    }
}
