package uk.joshiejack.simplyseasons.scripting;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import uk.joshiejack.penguinlib.event.ScriptingEvents;
import uk.joshiejack.penguinlib.scripting.wrapper.LevelJS;
import uk.joshiejack.penguinlib.util.IModPlugin;
import uk.joshiejack.penguinlib.util.helper.MathHelper;
import uk.joshiejack.penguinlib.util.registry.Plugin;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.CalendarDate;

import java.util.Optional;

@Plugin("rhino")
public class SeasonScripting implements IModPlugin {
    @Override
    public void setup() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCollectGlobalScripting(ScriptingEvents.CollectGlobalVarsAndFunctions event) {
        event.registerVar("season", this);
        event.registerEnum(Season.class);
    }

    public Season fromID(int id) {
        return MathHelper.getArrayValue(Season.MAIN, id);
    }

    public Season get(LevelJS<?> levelJS) {
        Optional<ISeasonProvider> provider = SSeasonsAPI.instance().getSeasonProvider(levelJS.get().dimension());
        return provider.map(s -> s.getSeason(levelJS.get())).orElse(Season.SPRING);
    }

    public boolean is(LevelJS<?> levelJS, int id) {
        return get(levelJS) == MathHelper.getArrayValue(Season.MAIN, id);
    }

    public int asYear(LevelJS<?> level, long time) {
        return CalendarDate.getYear(level.get(), time);
    }
}