package uk.joshiejack.simplyseasons.scripting;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import uk.joshiejack.penguinlib.event.ScriptingEvents;
import uk.joshiejack.penguinlib.scripting.wrapper.LevelJS;
import uk.joshiejack.penguinlib.util.IModPlugin;
import uk.joshiejack.penguinlib.util.registry.Plugin;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Weather;
import uk.joshiejack.simplyseasons.world.weather.AbstractWeatherProvider;

import java.util.Optional;

@Plugin("rhino")
public class WeatherScripting implements IModPlugin {
    @Override
    public void setup() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCollectGlobalScripting(ScriptingEvents.CollectGlobalVarsAndFunctions event) {
        event.registerVar("weather", this);
        event.registerEnum(Weather.class);
    }

    public Weather today(LevelJS<?> levelJS) {
        Optional<IWeatherProvider> provider = SSeasonsAPI.instance().getWeatherProvider(levelJS.get().dimension());
        return provider.map(s -> s.getWeather(levelJS.get()))
                .orElse(Weather.CLEAR);
    }

    public Weather tomorrow(LevelJS<?> levelJS) {
        Optional<IWeatherProvider> provider = SSeasonsAPI.instance().getWeatherProvider(levelJS.get().dimension());
        return provider.filter(s -> s instanceof AbstractWeatherProvider)
                .map(s -> ((AbstractWeatherProvider) s).getForecast(levelJS.get()))
                .orElse(Weather.CLEAR);
    }
}