package uk.joshiejack.simplyseasons.plugins;

import corgitaco.betterweather.BetterWeather;
import corgitaco.betterweather.api.SeasonData;
import corgitaco.betterweather.datastorage.BetterWeatherSeasonData;
import corgitaco.betterweather.season.SeasonSystem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.season.AbstractSeasonsProvider;

import java.util.*;

public class BetterWeatherSeasonProvider extends AbstractSeasonsProvider {
    private static final Map<SeasonData.SeasonVal, Season> SEASON_MAPPER = new HashMap<>();
    static {
        for (SeasonData.SeasonVal season : SeasonData.SeasonVal.values()) {
            SEASON_MAPPER.put(season, Season.valueOf(season.name()));
        }
    }

    @Override
    public Season getSeason(World world) {
        return SEASON_MAPPER.get(SeasonData.currentSeason);
    }

    @Override
    public void setSeason(World world, Season season) {
        //TODO: Use command code instead
        try {
            BetterWeatherSeasonData.get(world).setForced(true);
            BetterWeatherSeasonData.get(world).setSeasonTime(SeasonSystem.getTimeInCycleForSubSeason(season == Season.SPRING ? SeasonData.SubSeasonVal.SPRING_START
                    : season == Season.SUMMER ? SeasonData.SubSeasonVal.SUMMER_START
                    : season == Season.AUTUMN ? SeasonData.SubSeasonVal.AUTUMN_START
                    : SeasonData.SubSeasonVal.WINTER_START, BetterWeather.SEASON_CYCLE_LENGTH));
        } catch (Exception ignored) {}
    }

    @Override
    public Set<Season> getSeasonsAt(World world, BlockPos pos) {
        Set<Season> seasons = EnumSet.noneOf(Season.class);
        SSeasonsAPI.LOCALIZED_SEASON_HANDLER.forEach(local ->
                seasons.addAll(local.getSeasonsAt(world, pos)));
        return seasons.isEmpty() ? Collections.singleton(getSeason(world)) : seasons;
    }
}
