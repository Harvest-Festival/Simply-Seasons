package uk.joshiejack.simplyseasons.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface IWeatherProvider {
    /**
     * Get the weather for this world
     * @param world the world
     * @return  the weather
     */
    Weather getWeather(Level world);

    /**
     * Set the weather for this world
     * @param world     the world
     * @param weather   the weather to set
     */
    void setWeather(Level world, Weather weather);

    /**
     * Ticks the world, allowing it to update the weather
     * @param world     the world
     */
    void tick(ServerLevel world);
}