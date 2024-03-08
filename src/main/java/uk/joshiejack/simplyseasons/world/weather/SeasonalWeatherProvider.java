package uk.joshiejack.simplyseasons.world.weather;

import net.minecraft.world.level.Level;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Weather;
public class SeasonalWeatherProvider extends AbstractWeatherProvider {
    public SeasonalWeatherProvider(Weather defaultWeather, int frequency, int chance) {
        super(defaultWeather, frequency, chance);
    }

    @Override
    protected Weather getRandom(Level world) {
        ISeasonProvider provider = SSeasonsAPI.instance().getSeasonProvider(world.dimension()).orElse(null);
        if (provider != null)
            return WeatheredWorlds.getRandomWeatherForSeason(provider.getSeason(world), world.random);
        else
            return WeatheredWorlds.getRandomWeatherForSeason(null, world.random);
    }
}
