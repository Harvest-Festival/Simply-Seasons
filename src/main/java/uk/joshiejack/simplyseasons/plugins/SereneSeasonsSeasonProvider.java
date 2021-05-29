package uk.joshiejack.simplyseasons.plugins;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.handler.season.SeasonHandler;
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
    public Season getSeason(World world) {
        return SEASON_MAPPER.get(SeasonHelper.getSeasonState(world).getSeason());
    }

    @Override
    public void setSeason(World world, Season season) {
        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
        seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * (season.ordinal() * 3);
        seasonData.setDirty();
        SeasonHandler.sendSeasonUpdate(world);
    }

    @Override
    public Set<Season> getSeasonsAt(World world, BlockPos pos) {
        Set<Season> seasons = EnumSet.noneOf(Season.class);
        SSeasonsAPI.LOCALIZED_SEASON_HANDLER.forEach(local ->
                seasons.addAll(local.getSeasonsAt(world, pos)));
        return seasons.isEmpty() ? Collections.singleton(getSeason(world)) : seasons;
    }
}
