package uk.joshiejack.simplyseasons.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.client.renderer.SeasonsHUDRender;

import javax.annotation.Nullable;

public final class SSItemTags extends ItemTagsProvider {
    public SSItemTags(DataGenerator generator, BlockTagsProvider blockTagProvider,@Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagProvider, SimplySeasons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(SeasonsHUDRender.CALENDARS).addOptional(new ResourceLocation("sereneseasons", "calendar"));
    }
}
