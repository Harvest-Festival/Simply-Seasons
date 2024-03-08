package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClientConfig;

import java.util.List;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
public class SeasonDebugOverlay {
    private static int index = -1;

    //TODO: Move this to a mixin so that we can actually put it after biome like we want to...
    @SubscribeEvent
    public static void onDebugOverlay(CustomizeGuiOverlayEvent.DebugText event) {
        if (!SSClientConfig.seasonInDebug.get()) return;
        Minecraft mc = Minecraft.getInstance();
        //if (mc.options.renderDebug) {
            assert mc.level != null;
            //findBiomeInsertionPoint(event.getLeft());
            Season season = SeasonsHUDRender.getSeason(mc.level);
            if (season != null)
                event.getLeft().add("Season: " + season.name());
        //}
    }

    private static void findBiomeInsertionPoint(List<String> list) {
        if (index == -1) {
            for (int i = 0; i < list.size(); i++) {
                String text = list.get(i);
                if (text.startsWith("Biome")) {
                    index = i + 1;
                    break;
                }
            }
        }
    }
}