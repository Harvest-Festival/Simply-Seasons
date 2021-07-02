package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClientConfig;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
public class SeasonDebugOverlay {
    private static int index = -1;

    @SubscribeEvent
    public static void onDebugOverlay(RenderGameOverlayEvent.Text event) {
        if (!SSClientConfig.seasonInDebug.get()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.renderDebug) {
            assert mc.level != null;
            findBiomeInsertionPoint(event.getLeft());
            Season season = SeasonsHUDRender.getSeason(mc.level);
            if (season != null && index != -1)
                event.getLeft().add(index, "Season: " + season.name());
        }
    }

    private static void findBiomeInsertionPoint(ArrayList<String> list) {
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