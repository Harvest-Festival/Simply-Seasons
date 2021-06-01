package uk.joshiejack.simplyseasons.api;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IWeatherProvider extends ICapabilityProvider {
    /**
     * Get the weather for this world
     * @param world the world
     * @return  the weather
     */
    Weather getWeather(World world);

    /**
     * Set the weather for this world
     * @param world     the world
     * @param weather   the weather to set
     */
    void setWeather(World world, Weather weather);

    /**
     * Ticks the world, allowing it to update the weather
     * @param world     the world
     */
    void tick(ServerWorld world);
}