package uk.joshiejack.simplyseasons.world.season;

import net.minecraft.ChatFormatting;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;

import java.util.EnumMap;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public record SeasonData(ChatFormatting hud, float temperature, int leaves, int grass, int sky, long sunrise, long sunset) {
    private static final EnumMap<Season, SeasonData> DATA = new EnumMap<>(Season.class);
    private static final SeasonData EMPTY = new SeasonData(ChatFormatting.WHITE, 0F, 0, 0, 0, 0, 0);

    public static SeasonData get(Season season) {
        return DATA.getOrDefault(season, EMPTY);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) { //HIGHEST
        //Load in the season data
        event.table("seasons_data").rows().forEach(row ->
                SeasonData.DATA.put(Season.valueOf(row.get("season").toString().toUpperCase(Locale.ENGLISH)),
                        new SeasonData(ChatFormatting.getByName(row.get("hud").toString()), row.getAsFloat("temperature"),
                                row.getColor("leaves"), row.getColor("grass"), row.getColor("sky"),
                                row.getAsLong("sunrise"), row.getAsLong("sunset"))));
    }
}
