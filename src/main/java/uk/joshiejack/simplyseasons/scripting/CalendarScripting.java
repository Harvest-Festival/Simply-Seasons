package uk.joshiejack.simplyseasons.scripting;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import uk.joshiejack.penguinlib.event.ScriptingEvents;
import uk.joshiejack.penguinlib.scripting.wrapper.LevelJS;
import uk.joshiejack.penguinlib.util.IModPlugin;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.penguinlib.util.registry.Plugin;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.world.CalendarDate;

import java.time.DayOfWeek;
import java.util.Optional;

@Plugin("rhino")
public class CalendarScripting implements IModPlugin {
    @Override
    public void setup() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCollectGlobalScripting(ScriptingEvents.CollectGlobalVarsAndFunctions event) {
        event.registerVar("calendar", this);
        event.registerEnum(DayOfWeek.class);
    }

    public int day(LevelJS<?> worldJS) {
        Optional<ISeasonProvider> provider = SSeasonsAPI.instance().getSeasonProvider(worldJS.get().dimension());
        return provider.map(s -> s.getDay(worldJS.get())).orElse(TimeHelper.getElapsedDays(worldJS.get()));
    }

    public DayOfWeek weekday(LevelJS<?> level) {
        return TimeHelper.getWeekday(level.get().getDayTime());
    }

    public int year(LevelJS<?> levelJS) {
        return CalendarDate.getYear(levelJS.get());
    }

    public int elapsed(LevelJS<?> world) {
        return TimeHelper.getElapsedDays(world.get());
    }
}