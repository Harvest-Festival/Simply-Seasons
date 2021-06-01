package uk.joshiejack.simplyseasons.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import uk.joshiejack.simplyseasons.SimplySeasons;

public class SSLanguage extends LanguageProvider {
    public SSLanguage(DataGenerator gen) {
        super(gen, SimplySeasons.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("simplyseasons.hud", "%1$s %2$s");
        add("simplyseasons.spring", "Spring");
        add("simplyseasons.summer", "Summer");
        add("simplyseasons.autumn", "Autumn");
        add("simplyseasons.winter", "Winter");
        add("simplyseasons.wet", "Wet Season");
        add("simplyseasons.dry", "Dry Season");
        add("command.simplyseasons.set_season.spring", "Set the season to spring");
        add("command.simplyseasons.set_season.summer", "Set the season to summer");
        add("command.simplyseasons.set_season.autumn", "Set the season to autumn");
        add("command.simplyseasons.set_season.winter", "Set the season to winter");
        add("command.simplyseasons.no_seasons_world", "Seasons are not controlled by Simply Seasons in this world");
        add("command.simplyseasons.clear", "Set the weather to clear");
        add("command.simplyseasons.rain", "Set the weather to rain");
        add("command.simplyseasons.storm", "Set the weather to storm");
        add("command.simplyseasons.fog", "Set the weather to fog");
        add("command.simplyseasons.no_weather_world", "Weather is not controlled by Simply Seasons in this world");
        add("command.simplyseasons.weather.disabled", "Please use the /simplyseasons weather command instead");
    }
}