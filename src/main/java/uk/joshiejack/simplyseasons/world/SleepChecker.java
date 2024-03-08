package uk.joshiejack.simplyseasons.world;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.SleepingTimeCheckEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.season.SeasonData;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SleepChecker {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSlept(SleepFinishedTimeEvent event) {
        if (SimplySeasons.SSConfig.wakePlayerUpAtSunrise.get()) {
            if (event.getLevel() instanceof Level level) {
                SSeasonsAPI.instance().getSeasonProvider(level.dimension()).ifPresent(provider -> {
                    Season season = provider.getSeason(level);
                    SeasonData data = SeasonData.get(season);
                    long sunrise = 6000 - data.sunrise();
                    event.setTimeAddition(event.getNewTime() - sunrise);
                });
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void checkSleepTime(SleepingTimeCheckEvent event) {
        long time = TimeHelper.getTimeOfDay(event.getEntity().level().getDayTime());
        if (SimplySeasons.SSConfig.sleepAtAnytime.get())
            event.setResult(Event.Result.ALLOW);
        else if (SimplySeasons.SSConfig.noSleepingBefore.get() >= 6000 && time >= 6000 && time <= SimplySeasons.SSConfig.noSleepingBefore.get()) {
            event.setResult(Event.Result.DENY);
        } else if (SimplySeasons.SSConfig.noSleepingBetweenWakeupAndSunset.get()) {
            SSeasonsAPI.instance().getSeasonProvider(event.getEntity().level().dimension()).ifPresent(provider -> {
                Season season = provider.getSeason(event.getEntity().level());
                SeasonData data = SeasonData.get(season);
                long sunrise = SimplySeasons.SSConfig.wakePlayerUpAtSunrise.get() ? data.sunrise() : 6000;
                if (time >= sunrise && time <= data.sunset())
                    event.setResult(Event.Result.DENY);
            });
        }
    }
}