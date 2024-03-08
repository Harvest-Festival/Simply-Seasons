package uk.joshiejack.simplyseasons.client.renderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClientConfig;
import uk.joshiejack.simplyseasons.world.loot.SeasonPredicate;
import uk.joshiejack.simplyseasons.world.season.SeasonData;
import uk.joshiejack.simplyseasons.world.season.SeasonalCrops;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SimplySeasons.MODID)
public class TooltipRenderer {
    @SubscribeEvent
    public static void onToolTip(ItemTooltipEvent event) {
        if (!SSClientConfig.enableCropsTooltip.get() || SeasonalCrops.isSimplySeasonsGrowthDisabled()) return;
        SeasonPredicate predicate = SeasonalCrops.itemPredicate(event.getItemStack().getItem());
        if (predicate != null) {
            predicate.seasons()
                    .filter(season -> SSClientConfig.showWetDryTooltip.get() || season.ordinal() <= Season.WINTER.ordinal())
                    .forEach(season -> event.getToolTip().add(SeasonsHUDRender.getName(season).copy().withStyle(SeasonData.get(season).hud())));
        }
    }
}