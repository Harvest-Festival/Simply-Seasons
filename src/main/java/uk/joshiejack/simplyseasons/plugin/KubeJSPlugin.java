package uk.joshiejack.simplyseasons.plugin;

import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.plugin.kubejs.SeasonUtils;

public class KubeJSPlugin extends dev.latvian.mods.kubejs.KubeJSPlugin {
    @Override
    public void registerClasses(ScriptType type, ClassFilter filter) {
        filter.allow("uk.joshiejack.simplyseasons.plugins.kubejs");
        filter.allow(Season.class);
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        if (event.getType() != ScriptType.STARTUP)
            event.add(SimplySeasons.MODID, SeasonUtils.class);
    }
}