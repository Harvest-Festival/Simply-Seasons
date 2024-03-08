package uk.joshiejack.simplyseasons.plugin;

import uk.joshiejack.penguinlib.util.IModPlugin;
import uk.joshiejack.penguinlib.util.registry.Plugin;

@Plugin("resourcefulbees")
public class ResourcefulBeesPlugin implements IModPlugin {
    public static boolean loaded = false;

    @Override
    public void setup() {
        loaded = true;
    }
}
