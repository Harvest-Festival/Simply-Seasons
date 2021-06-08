package uk.joshiejack.simplyseasons.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.simplyseasons.world.season.SeasonalCrops;

import javax.annotation.Nullable;

public final class SSBlockTags extends BlockTagsProvider {
    public SSBlockTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, PenguinLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(SeasonalCrops.JUNK).add(Blocks.DEAD_BUSH, Blocks.AIR);
    }
}
