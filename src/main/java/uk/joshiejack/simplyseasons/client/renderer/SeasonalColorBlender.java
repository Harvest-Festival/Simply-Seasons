package uk.joshiejack.simplyseasons.client.renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.plugin.SereneSeasonsPlugin;
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
    public static void onBlockColors(RegisterColorHandlersEvent.Block colors) {
        if (SereneSeasonsPlugin.loaded) return; //They can handle this
        colors.register((state, reader, pos, color) -> {
            Level world = Minecraft.getInstance().level;
            ISeasonProvider optional = SSeasonsAPI.instance().getSeasonProvider(world.dimension()).orElse(null);
            if (optional != null) {
                SeasonData data = SeasonData.get(optional.getSeason(world));
                return getBlendedColor(FoliageColor.getBirchColor(), data.leaves(), 2);
            }

            return FoliageColor.getBirchColor();
        }, Blocks.BIRCH_LEAVES);
    }

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        if (SereneSeasonsPlugin.loaded) return;  //They can handle this
        ColorResolver grass = BiomeColors.GRASS_COLOR_RESOLVER;
        ColorResolver foliage = BiomeColors.FOLIAGE_COLOR_RESOLVER;
        BiomeColors.GRASS_COLOR_RESOLVER = (biome, x, z) -> {
            Level world = Minecraft.getInstance().level;
            int original = grass.getColor(biome, x, z);
            ISeasonProvider optional = SSeasonsAPI.instance().getSeasonProvider(world.dimension()).orElse(null);
            if (optional != null) {
                SeasonData data = SeasonData.get(optional.getSeason(world));
                return getBlendedColor(original, data.grass(), 2);
            } else return original;
        };

        BiomeColors.FOLIAGE_COLOR_RESOLVER = (biome, x, z) -> {
            Level world = Minecraft.getInstance().level;
            int original = foliage.getColor(biome, x, z);
            ISeasonProvider optional = SSeasonsAPI.instance().getSeasonProvider(world.dimension()).orElse(null);
            if (optional != null) {
                Season season = optional.getSeason(world);
                SeasonData data = SeasonData.get(season);
                return getBlendedColor(original, data.leaves(), season == Season.AUTUMN ? 8 : 2);
            } else return original;
        };
    }
}
