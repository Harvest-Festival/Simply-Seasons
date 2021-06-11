package uk.joshiejack.simplyseasons.plugins;

import net.darkhax.botanypots.api.events.PotGrowCropEvent;
import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.penguinlib.util.interfaces.IModPlugin;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.loot.SeasonPredicate;
import uk.joshiejack.simplyseasons.world.season.SeasonalCrops;

import java.util.Objects;

@PenguinLoader("botanypots")
public class BotanyPotsPlugin implements IModPlugin {
    @Override
    public void setup() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBotanyPotGrow(PotGrowCropEvent event) {
        TileEntityBotanyPot pot = event.getBotanyPot();
        if (Objects.requireNonNull(pot.getLevel()).getDayTime() % 20 != 0 && !pot.getLevel().isClientSide) return;
        ItemStack seeds = pot.getCropStack();
        SeasonPredicate predicate = SeasonalCrops.ITEMS.get(seeds.getItem());
        if (predicate != null && !predicate.matches(pot.getLevel(), pot.getBlockPos())) {
            SeasonalCrops.CropOutOfSeasonEffect effect = SimplySeasons.SSConfig.cropOutOfSeasonEffect.get();
            switch (effect) {
                case SLOW_GROWTH:
                    if (SeasonalCrops.CropOutOfSeasonEffect.SLOW_GROWTH.predicate.test(pot.getLevel(), pot.getBlockPos()))
                        event.setCanceled(true);
                    break;
                case NO_GROWTH:
                    event.setCanceled(true);
                    break;
                case REPLACE_WITH_JUNK:
                case SET_TO_AIR:
                    if (pot.getLevel().getDayTime() %200 == 180)
                        pot.setCrop(null, ItemStack.EMPTY);
                    event.setCanceled(true);
                    break;
            }
        }
    }
}