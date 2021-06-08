package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.client.SSClientConfig;
import uk.joshiejack.simplyseasons.world.season.SeasonData;
import uk.joshiejack.simplyseasons.world.season.SeasonalCrops;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SimplySeasons.MODID)
public class TooltipRenderer {
    @SubscribeEvent
    public static void onToolTip(ItemTooltipEvent event) {
        if (!SSClientConfig.enableCropsTooltip.get()) return;
        if (SeasonalCrops.ITEMS.containsKey(event.getItemStack().getItem())) {
            SeasonalCrops.ITEMS.get(event.getItemStack().getItem()).seasons()
                    .forEach(season -> event.getToolTip().add(SeasonsHUDRender.getName(season).withStyle(SeasonData.get(season).hud)));
        }
    }
}