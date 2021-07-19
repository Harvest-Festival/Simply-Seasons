package uk.joshiejack.simplyseasons.world;

import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.util.helpers.TimeHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.season.SeasonData;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SleepChecker {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSlept(SleepFinishedTimeEvent event) {
        if (SimplySeasons.SSConfig.wakePlayerUpAtSunrise.get()) {
            if (event.getWorld() instanceof World) {
                ((World) event.getWorld()).getCapability(SSeasonsAPI.SEASONS_CAPABILITY).ifPresent(provider -> {
                    Season season = provider.getSeason((World) event.getWorld());
                    SeasonData data = SeasonData.get(season);
                    long sunrise = 6000 - data.sunrise;
                    event.setTimeAddition(event.getNewTime() - sunrise);
                });
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void checkSleepTime(SleepingTimeCheckEvent event) {
        long time = TimeHelper.getTimeOfDay(event.getPlayer().level.getDayTime());
        if (SimplySeasons.SSConfig.sleepAtAnytime.get())
            event.setResult(Event.Result.ALLOW);
        else if (SimplySeasons.SSConfig.noSleepingBefore.get() >= 6000 && time >= 6000 && time <= SimplySeasons.SSConfig.noSleepingBefore.get()) {
            event.setResult(Event.Result.DENY);
        } else if (SimplySeasons.SSConfig.noSleepingBetweenWakeupAndSunset.get()) {
            event.getPlayer().level.getCapability(SSeasonsAPI.SEASONS_CAPABILITY).ifPresent(provider -> {
                Season season = provider.getSeason(event.getPlayer().level);
                SeasonData data = SeasonData.get(season);
                long sunrise = SimplySeasons.SSConfig.wakePlayerUpAtSunrise.get() ? data.sunrise : 6000;
                if (time >= sunrise && time <= data.sunset)
                    event.setResult(Event.Result.DENY);
            });
        }
    }
}