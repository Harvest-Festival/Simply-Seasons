package uk.joshiejack.simplyseasons.world.weather;

import net.minecraft.world.World;

public class RandomWeatherProvider extends AbstractWeatherProvider  {
    public RandomWeatherProvider(int frequency, int chance) {
        super(frequency, chance);
    }

    @Override
    protected Weather getRandom(World world) {
        return WeatheredWorlds.getRandomWeatherForWorld(world.dimension(), world.random);
    }
}
