package uk.joshiejack.simplyseasons.world.weather;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.events.DatabaseLoadedEvent;
import uk.joshiejack.penguinlib.util.loot.LootRegistry;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.api.Weather;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class WeatheredWorlds {
    private static final Map<RegistryKey<World>, IWeatherProvider> PROVIDERS = new HashMap<>();
    private static final Map<Season, LootRegistry<Weather>> SEASONAL_WEATHER_MAP = new EnumMap<>(Season.class);
    private static final Map<RegistryKey<World>, LootRegistry<Weather>> RANDOM_WEATHER_MAP = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttachCapability(AttachCapabilitiesEvent<World> event) {
        if (PROVIDERS.containsKey(event.getObject().dimension()))
            event.addCapability(new ResourceLocation(SimplySeasons.MODID, "weather"), PROVIDERS.get(event.getObject().dimension()));
    }

    @SubscribeEvent
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        if (BetterWeatherPlugin.loaded) return;
        PROVIDERS.clear();
        event.table("weathered_worlds").rows().forEach(row -> {
            RegistryKey<World> world = RegistryKey.create(Registry.DIMENSION_REGISTRY, row.getRL("world"));
            String type = row.get("type");
            if (type.equals("seasonal"))
                PROVIDERS.put(world, new SeasonalWeatherProvider(Weather.valueOf(row.get("default weather").toString().toUpperCase(Locale.ENGLISH)), row.getAsInt("frequency"), row.getAsInt("chance")));
            if (type.equals("random"))
                PROVIDERS.put(world, new RandomWeatherProvider(Weather.valueOf(row.get("default weather").toString().toUpperCase(Locale.ENGLISH)), row.getAsInt("frequency"), row.getAsInt("chance")));
        });

        SEASONAL_WEATHER_MAP.clear();
        event.table("seasonal_weather").rows().forEach(row -> {
            Season season = Season.valueOf(row.get("season").toString().toUpperCase(Locale.ENGLISH));
            Weather weather = Weather.valueOf(row.get("weather").toString().toUpperCase(Locale.ENGLISH));
            if (!SEASONAL_WEATHER_MAP.containsKey(season))
                SEASONAL_WEATHER_MAP.put(season, new LootRegistry<>());
            SEASONAL_WEATHER_MAP.get(season).add(weather, row.getAsDouble("weight"));
        });

        RANDOM_WEATHER_MAP.clear();
        event.table("random_weather").rows().forEach(row -> {
            RegistryKey<World> world = RegistryKey.create(Registry.DIMENSION_REGISTRY, row.getRL("world"));
            Weather weather = Weather.valueOf(row.get("weather").toString().toUpperCase(Locale.ENGLISH));
            if (!RANDOM_WEATHER_MAP.containsKey(world))
                RANDOM_WEATHER_MAP.put(world, new LootRegistry<>());
            RANDOM_WEATHER_MAP.get(world).add(weather, row.getAsDouble("weight"));
        });
    }

    public static Weather getRandomWeatherForSeason(@Nullable Season season, Random rand) {
        if (season == null || !SEASONAL_WEATHER_MAP.containsKey(season))
            return Weather.values()[rand.nextInt(Weather.values().length)];
        return SEASONAL_WEATHER_MAP.get(season).get(rand);
    }

    public static Weather getRandomWeatherForWorld(RegistryKey<World> dimension, Random rand) {
        if (dimension == null || !RANDOM_WEATHER_MAP.containsKey(dimension))
            return Weather.values()[rand.nextInt(Weather.values().length)];
        return RANDOM_WEATHER_MAP.get(dimension).get(rand);
    }
}
