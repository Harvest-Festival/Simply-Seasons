package uk.joshiejack.simplyseasons.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.world.season.SeasonalCrops;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

import java.util.concurrent.CompletableFuture;

public final class SSBlockTags extends BlockTagsProvider {
    public SSBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SimplySeasons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(SeasonalCrops.JUNK).add(Blocks.DEAD_BUSH, Blocks.AIR);
        tag(SeasonalCrops.INDESTRUCTIBLE).add(Blocks.SWEET_BERRY_BUSH, Blocks.CACTUS, Blocks.SUGAR_CANE, Blocks.BAMBOO, Blocks.COCOA);
        tag(SeasonalWorlds.SPRING_NO_BONE_MEAL_BLOCKS)
                .addOptional(new ResourceLocation("byg", "meadow_grass_block"))
                .addOptional(new ResourceLocation("byg", "podzol_dacite"))
                .addOptional(new ResourceLocation("byg", "overgrown_dacite"))
                .addOptional(new ResourceLocation("byg", "overgrown_stone"));
    }
}
