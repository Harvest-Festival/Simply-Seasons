package uk.joshiejack.simplyseasons.plugins;

import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.penguinlib.util.interfaces.IModPlugin;

@PenguinLoader("resourcefulbees")
public class ResourcefulBees implements IModPlugin {
    public static boolean loaded = false;

    @Override
    public void setup() {
        loaded = true;
    }
}
