package uk.joshiejack.simplyseasons.plugin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.season.AbstractSeasonsProvider;

import java.util.*;

public class SereneSeasonsSeasonProvider extends AbstractSeasonsProvider {
    private static final Map<sereneseasons.api.season.Season, Season> SEASON_MAPPER = new HashMap<>();
    static {
        for (sereneseasons.api.season.Season season: sereneseasons.api.season.Season.values()) {
            SEASON_MAPPER.put(season, Season.valueOf(season.name()));
        }
    }

    @Override
    public int getDay(Level world) {
        ISeasonState state = SeasonHelper.getSeasonState(world);
        return 1 + ((state.getDay()) % (state.getSeasonDuration() / state.getDayDuration()));
    }

    @Override
    public Season getSeason(Level world) {
        return SEASON_MAPPER.get(SeasonHelper.getSeasonState(world).getSeason());
    }

    @Override
    public void setSeason(Level world, Season season) {
        try {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
            seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * (season.ordinal() * 3);
            seasonData.setDirty();
            SeasonHandler.sendSeasonUpdate(world);
        } catch (Exception ignored) {}
    }

    @Override
    public Set<Season> getSeasonsAt(Level world, BlockPos pos) {
        Set<Season> seasons = EnumSet.noneOf(Season.class);
        SSeasonsAPI.LOCALIZED_SEASON_HANDLER.forEach(local ->
                seasons.addAll(local.getSeasonsAt(world, pos)));
        return seasons.isEmpty() ? Collections.singleton(getSeason(world)) : seasons;
    }
}
