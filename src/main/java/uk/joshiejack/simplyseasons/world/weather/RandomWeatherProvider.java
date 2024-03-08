package uk.joshiejack.simplyseasons.world.weather;

import net.minecraft.world.level.Level;
import uk.joshiejack.simplyseasons.api.Weather;


public class RandomWeatherProvider extends AbstractWeatherProvider  {
    public RandomWeatherProvider(Weather defaultWeather, int frequency, int chance) {
        super(defaultWeather, frequency, chance);
    }

    @Override
    protected Weather getRandom(Level world) {
        return WeatheredWorlds.getRandomWeatherForWorld(world.dimension(), world.random);
    }
}
