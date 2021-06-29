package uk.joshiejack.simplyseasons.plugins;

import com.google.common.collect.Sets;
import net.minecraft.block.AbstractGlassBlock;
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
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.SSServerConfig;

import java.util.Set;
import java.util.stream.IntStream;

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
        final Set<Season> all = Sets.newHashSet(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER, Season.WET, Season.DRY);
        final Set<Season> none = Sets.newHashSet();
        SSeasonsAPI.LOCALIZED_SEASON_HANDLER.add((world, pos) -> SSServerConfig.useSSCropsHandler.get()
                && IntStream.rangeClosed(0, 15)
                .anyMatch(i -> world.getBlockState(pos.offset(0, i + 1, 0)).getBlock() instanceof AbstractGlassBlock) ? all : none);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttachCapability(AttachCapabilitiesEvent<World> event) {
        if (isWhitelisted(event.getObject().dimension()))
            event.addCapability(new ResourceLocation(SimplySeasons.MODID, "seasons"), provider);
    }

    private boolean isWhitelisted(RegistryKey<World> world) {
        return SeasonsConfig.isDimensionWhitelisted(world);
    }
}