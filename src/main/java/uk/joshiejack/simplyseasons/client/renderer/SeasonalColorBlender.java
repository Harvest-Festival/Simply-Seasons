package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.plugins.SereneSeasonsPlugin;
import uk.joshiejack.simplyseasons.world.season.SeasonData;


@SuppressWarnings("ConstantConditions")
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SeasonalColorBlender {
    public static int getBlendedColor(int original, int additional, int size) {
        if (additional == 0) return original;
        try {
            int r = (original & 0xFF0000) >> 16;
            int g = (original & 0x00FF00) >> 8;
            int b = original & 0x0000FF;
            for (int i = 1; i < size; i++) {
                r += (additional & 0xFF0000) >> 16;
                g += (additional & 0x00FF00) >> 8;
                b += additional & 0x0000FF;
            }

            return (r / size & 255) << 16 | (g / size & 255) << 8 | b / size & 255;
        } catch (IndexOutOfBoundsException exception) {
            return original;
        }
    }

    @SubscribeEvent
    public static void onBlockColors(ColorHandlerEvent.Block colors) {
        if (SereneSeasonsPlugin.loaded) return; //They can handle this
        colors.getBlockColors().register((state, reader, pos, color) -> {
            World world = Minecraft.getInstance().level;
            LazyOptional<ISeasonsProvider> optional = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
            if (optional.isPresent()) {
                SeasonData data = SeasonData.get(optional.resolve().get().getSeason(world));
                return getBlendedColor(FoliageColors.getBirchColor(), data.leaves, 2);
            }

            return FoliageColors.getBirchColor();
        }, Blocks.BIRCH_LEAVES);
    }

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        if (SereneSeasonsPlugin.loaded) return;  //They can handle this
        ColorResolver grass = BiomeColors.GRASS_COLOR_RESOLVER;
        ColorResolver foliage = BiomeColors.FOLIAGE_COLOR_RESOLVER;
        BiomeColors.GRASS_COLOR_RESOLVER = (biome, x, z) -> {
            World world = Minecraft.getInstance().level;
            int original = grass.getColor(biome, x, z);
            LazyOptional<ISeasonsProvider> optional = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
            if (optional.isPresent()) {
                SeasonData data = SeasonData.get(optional.resolve().get().getSeason(world));
                return getBlendedColor(original, data.grass, 2);
            } else return original;
        };

        BiomeColors.FOLIAGE_COLOR_RESOLVER = (biome, x, z) -> {
            World world = Minecraft.getInstance().level;
            int original = foliage.getColor(biome, x, z);
            LazyOptional<ISeasonsProvider> optional = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
            if (optional.isPresent()) {
                Season season = optional.resolve().get().getSeason(world);
                SeasonData data = SeasonData.get(season);
                return getBlendedColor(original, data.leaves, season == Season.AUTUMN ? 4 : 2);
            } else return original;
        };
    }
}
