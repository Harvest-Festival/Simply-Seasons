package uk.joshiejack.simplyseasons.world.weather;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.events.DatabaseLoadedEvent;
import uk.joshiejack.penguinlib.util.loot.LootRegistry;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class WeatherRegistry {
    private static final Map<Season, LootRegistry<Weather>> seasonWeatherMap = new EnumMap<>(Season.class);

    @SubscribeEvent
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        seasonWeatherMap.clear();
        event.table("weather").rows().forEach(row -> {
            Season season = Season.valueOf(row.get("season").toString().toUpperCase(Locale.ENGLISH));
            Weather weather = Weather.valueOf(row.get("weather").toString().toUpperCase(Locale.ENGLISH));
            if (!seasonWeatherMap.containsKey(season)) {
                seasonWeatherMap.put(season, new LootRegistry<>());
            }

            seasonWeatherMap.get(season).add(weather, row.getAsDouble("weight"));
        });
    }

    public static Weather getRandomWeatherForSeason(Season season, Random rand) {
        if (!seasonWeatherMap.containsKey(season)) {
            return Weather.values()[rand.nextInt(Weather.values().length)];
        }

        return seasonWeatherMap.get(season).get(rand);
    }
}
