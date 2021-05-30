package uk.joshiejack.simplyseasons.world.season;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.data.database.CSVUtils;
import uk.joshiejack.penguinlib.events.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.plugins.SereneSeasonsPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SeasonalWorlds {
    private static final Map<RegistryKey<World>, ISeasonsProvider> PROVIDERS = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttachCapability(AttachCapabilitiesEvent<World> event) {
        if (PROVIDERS.containsKey(event.getObject().dimension()))
            event.addCapability(new ResourceLocation(SimplySeasons.MODID, "seasons"), PROVIDERS.get(event.getObject().dimension()));
    }

    @SubscribeEvent
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        if (SereneSeasonsPlugin.loaded) return; //They will handle which worlds have seasons
        PROVIDERS.clear(); //Clear out the existing providers, as we're reloading
        event.table("seasonal_worlds").rows().forEach(row -> {
            RegistryKey<World> world = RegistryKey.create(Registry.DIMENSION_REGISTRY, row.getRL("world"));
            PROVIDERS.put(world, new SeasonsProvider(CSVUtils.parse(row.get("seasons")).stream()
                    .map(string -> Season.valueOf(string.toUpperCase(Locale.ROOT))).toArray(Season[]::new)));
        });
    }

    public static float getTemperature(World world, Biome biome, BlockPos pos) {
        LazyOptional<ISeasonsProvider> provider = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (provider.isPresent()) {
            return biome.getTemperature(pos) + SeasonData.get(provider.resolve().get().getSeason(world)).temperature;
        }

        return biome.getTemperature(pos);
    }
}