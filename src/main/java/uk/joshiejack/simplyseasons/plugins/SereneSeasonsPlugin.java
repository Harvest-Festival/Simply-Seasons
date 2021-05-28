package uk.joshiejack.simplyseasons.plugins;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sereneseasons.config.SeasonsConfig;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.penguinlib.util.interfaces.IModPlugin;
import uk.joshiejack.simplyseasons.SimplySeasons;

@PenguinLoader("sereneseasons")
public class SereneSeasonsPlugin implements IModPlugin {
    public static boolean loaded;
    private final SereneSeasonsSeasonProvider provider;

    public SereneSeasonsPlugin() {
        this.provider = new SereneSeasonsSeasonProvider();
    }

    @Override
    public void setup() {
        //Register season providers, using serene seasons data
        SereneSeasonsPlugin.loaded = true;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttachCapability(AttachCapabilitiesEvent<World> event) {
        if (isWhitelisted(event.getObject().dimension()))
            event.addCapability(new ResourceLocation(SimplySeasons.MODID, "seasons"), provider);
    }

    private boolean isWhitelisted(RegistryKey<World> world) {
        //TODO: Reflection instead?
        return SeasonsConfig.isDimensionWhitelisted(world);
    }
}
