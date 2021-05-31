package uk.joshiejack.simplyseasons.api;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import uk.joshiejack.simplyseasons.world.weather.Weather;

public interface IWeatherProvider extends ICapabilityProvider {
    Weather getWeather(World world);
    void setWeather(World world, Weather weather);
    void tick(ServerWorld world);
}