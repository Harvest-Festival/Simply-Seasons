package uk.joshiejack.simplyseasons.plugins;

import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassFilter;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.plugins.kubejs.SeasonUtils;

public class KubeJSPlugin extends dev.latvian.kubejs.KubeJSPlugin {
    @Override
    public void addClasses(ScriptType type, ClassFilter filter) {
        filter.allow("uk.joshiejack.simplyseasons.plugins.kubejs");
        filter.allow(Season.class);
    }

    @Override
    public void addBindings(BindingsEvent event) {
        if (event.type != ScriptType.STARTUP)
            event.addClass(SimplySeasons.MODID, SeasonUtils.class);
    }
}