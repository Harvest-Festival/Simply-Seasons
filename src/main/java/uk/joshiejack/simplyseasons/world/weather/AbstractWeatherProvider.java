package uk.joshiejack.simplyseasons.world.weather;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.Weather;
import uk.joshiejack.simplyseasons.network.WeatherChangedPacket;
import uk.joshiejack.simplyseasons.world.SSSavedData;

public abstract class AbstractWeatherProvider implements IWeatherProvider {
    protected Weather current;
    protected Weather forecast;
    protected final int updateFrequency;
    protected final int changeChance;

    public AbstractWeatherProvider(Weather defaultWeather, int frequency, int chance) {
        this.updateFrequency = frequency;
        this.changeChance = chance;
        this.current = defaultWeather;
        this.forecast = defaultWeather;
    }

    @Override
    public Weather getWeather(Level world) {
        return current;
    }

    protected abstract Weather getRandom(Level world);

    public Weather getForecast(Level world) {
        return forecast;
    }

    @Override
    public void setWeather(Level world, Weather weather) {
        this.current = weather;
        if (!world.isClientSide) {
            ServerLevel sWorld = (ServerLevel) world;
            if (current == Weather.CLEAR || current == Weather.FOG)
                sWorld.setWeatherParameters(Integer.MAX_VALUE, 0, false, false);
            else if (current == Weather.RAIN)
                sWorld.setWeatherParameters(0, Integer.MAX_VALUE, true, false);
            else if (current == Weather.STORM)
                sWorld.setWeatherParameters(0, Integer.MAX_VALUE, true, true);
            PenguinNetwork.sendToDimension(new WeatherChangedPacket(current), (ServerLevel) world);
            SSSavedData.get(sWorld).setDirty();
        }
    }

    @Override
    public void tick(ServerLevel world) {
        if (world.getDayTime() % updateFrequency == 1 || (changeChance > 1 && world.random.nextInt(changeChance) == 0)) {
            setWeather(world, forecast); //Change the current weather to the forecast one
            forecast = getRandom(world); //Grab a new forecasted weather
        }
    }

    public void load(CompoundTag nbt) {
        if (nbt.contains("Current")) {
            current = Weather.values()[nbt.getByte("Current")];
            forecast = Weather.values()[nbt.getByte("Forecast")];
        }
    }

    public void save(CompoundTag nbt) {
        if (current != null) {
            nbt.putByte("Current", (byte) current.ordinal());
            nbt.putByte("Forecast", (byte) forecast.ordinal());
        }
    }
}
