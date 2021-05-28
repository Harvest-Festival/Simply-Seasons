package uk.joshiejack.simplyseasons.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.world.weather.Weather;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSClient {
    public static final SSClient INSTANCE = new SSClient();
    private Weather weather = Weather.CLEAR;


    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
