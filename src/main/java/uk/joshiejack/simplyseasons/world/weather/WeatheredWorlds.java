package uk.joshiejack.simplyseasons.world.weather;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.penguinlib.util.random.LootRegistry;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.api.Weather;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class WeatheredWorlds {
    private static final Map<ResourceKey<Level>, IWeatherProvider> PROVIDERS = new HashMap<>();
    private static final Map<Season, LootRegistry<Weather>> SEASONAL_WEATHER_MAP = new EnumMap<>(Season.class);
    private static final Map<ResourceKey<Level>, LootRegistry<Weather>> RANDOM_WEATHER_MAP = new HashMap<>();

    public static Optional<IWeatherProvider> getWeatherProvider(ResourceKey<Level> level) {
        return Optional.ofNullable(PROVIDERS.get(level));
    }

    @SubscribeEvent
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        PROVIDERS.clear();
        event.table("weathered_worlds").rows().forEach(row -> {
            ResourceKey<Level> world = ResourceKey.create(Registries.DIMENSION, row.getRL("world"));
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
            ResourceKey<Level> world = ResourceKey.create(Registries.DIMENSION, row.getRL("world"));
            Weather weather = Weather.valueOf(row.get("weather").toString().toUpperCase(Locale.ENGLISH));
            if (!RANDOM_WEATHER_MAP.containsKey(world))
                RANDOM_WEATHER_MAP.put(world, new LootRegistry<>());
            RANDOM_WEATHER_MAP.get(world).add(weather, row.getAsDouble("weight"));
        });
    }

    public static Weather getRandomWeatherForSeason(@Nullable Season season, RandomSource rand) {
        if (season == null || !SEASONAL_WEATHER_MAP.containsKey(season))
            return Weather.values()[rand.nextInt(Weather.values().length)];
        return SEASONAL_WEATHER_MAP.get(season).get(rand);
    }

    public static Weather getRandomWeatherForWorld(ResourceKey<Level> dimension, RandomSource rand) {
        if (dimension == null || !RANDOM_WEATHER_MAP.containsKey(dimension))
            return Weather.values()[rand.nextInt(Weather.values().length)];
        return RANDOM_WEATHER_MAP.get(dimension).get(rand);
    }
}
