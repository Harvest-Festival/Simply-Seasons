package uk.joshiejack.simplyseasons.plugins;

import corgitaco.betterweather.config.BetterWeatherConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.penguinlib.util.interfaces.IModPlugin;
import uk.joshiejack.simplyseasons.SimplySeasons;

@PenguinLoader("betterweather")
public class BetterWeatherPlugin implements IModPlugin {
    public static boolean loaded;
    private final BetterWeatherSeasonProvider provider;

    public BetterWeatherPlugin() {
        this.provider = new BetterWeatherSeasonProvider();
    }

    @Override
    public void setup() {
        //Register season providers, using serene seasons data
        BetterWeatherPlugin.loaded = true;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttachCapability(AttachCapabilitiesEvent<World> event) {
        if (BetterWeatherConfig.SEASON_DIMENSIONS.contains(event.getObject().dimension().location().toString()))
            event.addCapability(new ResourceLocation(SimplySeasons.MODID, "seasons"), provider);
    }
}
