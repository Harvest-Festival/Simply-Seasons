package uk.joshiejack.simplyseasons.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.client.renderer.SeasonsHUDRender;

import java.util.concurrent.CompletableFuture;

public final class SSItemTags extends ItemTagsProvider {
    public SSItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagsProvider.TagLookup<Block>> blockLookup, ExistingFileHelper existingFileHelper) {
        super(output, provider, blockLookup, SimplySeasons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(SeasonsHUDRender.CALENDARS).addOptional(new ResourceLocation("sereneseasons", "calendar"));
    }
}
