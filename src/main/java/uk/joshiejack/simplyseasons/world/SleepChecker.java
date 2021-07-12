package uk.joshiejack.simplyseasons.world;

import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.util.helpers.TimeHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SleepChecker {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSleep(SleepingTimeCheckEvent event) {
        if (SimplySeasons.SSConfig.noSleepingBefore.get() >= 6000) {
            long time = TimeHelper.getTimeOfDay(event.getPlayer().level.getDayTime());
            if (time >= 6000 && time <= SimplySeasons.SSConfig.noSleepingBefore.get())
                event.setResult(Event.Result.DENY);
        }
    }
}