package uk.joshiejack.simplyseasons.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.world.season.SeasonalCrops;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

import javax.annotation.Nullable;

public final class SSBlockTags extends BlockTagsProvider {
    public SSBlockTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, SimplySeasons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(SeasonalCrops.JUNK).add(Blocks.DEAD_BUSH, Blocks.AIR);
        tag(SeasonalCrops.INDESTRUCTIBLE).add(Blocks.SWEET_BERRY_BUSH, Blocks.CACTUS, Blocks.SUGAR_CANE, Blocks.BAMBOO, Blocks.COCOA);
        tag(SeasonalWorlds.SPRING_NO_BONEMEAL_BLOCKS)
                .addOptional(new ResourceLocation("byg", "meadow_grass_block"))
                .addOptional(new ResourceLocation("byg", "podzol_dacite"))
                .addOptional(new ResourceLocation("byg", "overgrown_dacite"))
                .addOptional(new ResourceLocation("byg", "overgrown_stone"));
    }
}
