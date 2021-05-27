package uk.joshiejack.simplyseasons.world.season;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.events.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;

import java.util.EnumMap;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SeasonData {
    public static final EnumMap<Season, SeasonData> DATA = new EnumMap<>(Season.class);
    public final TextFormatting hud;
    public final int leaves;
    public final int grass;
    public final int sky;
    public final long sunrise;
    public final long sunset;
    public final float temperature;

    public SeasonData(TextFormatting hud, float temperature, int leaves, int grass, int sky, long sunrise, long sunset) {
        this.hud = hud;
        this.temperature = temperature;
        this.leaves = leaves;
        this.sky = sky;
        this.grass = grass;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) { //HIGHEST
        //Load in the season data
        event.table("seasons_data").rows().forEach(row ->
                SeasonData.DATA.put(Season.valueOf(row.get("season").toString().toUpperCase(Locale.ENGLISH)),
                        new SeasonData(TextFormatting.getByName(row.get("hud").toString()), row.getAsFloat("temperature"),
                                row.getColor("leaves"), row.getColor("grass"), row.getColor("sky"),
                                row.getAsLong("sunrise"), row.getAsLong("sunset"))));
    }
}
