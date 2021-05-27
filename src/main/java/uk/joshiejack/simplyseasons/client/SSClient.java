package uk.joshiejack.simplyseasons.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.date.CalendarDate;
import uk.joshiejack.simplyseasons.world.weather.Weather;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSClient {
    public static final SSClient INSTANCE = new SSClient();
    private CalendarDate date = new CalendarDate();
    private Season season = Season.SPRING;
    private Weather weather = Weather.CLEAR;

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        if (this.season != season) {
            //TODO: call for a render update
        }

        this.season = season;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public CalendarDate getDate() {
        return date;
    }

    public void setDate(CalendarDate date) {
        this.date.set(date);
    }
}
