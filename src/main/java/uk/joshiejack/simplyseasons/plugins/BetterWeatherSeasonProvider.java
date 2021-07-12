package uk.joshiejack.simplyseasons.plugins;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import corgitaco.betterweather.helpers.BetterWeatherWorldData;
import corgitaco.betterweather.season.SeasonContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.season.AbstractSeasonsProvider;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class BetterWeatherSeasonProvider extends AbstractSeasonsProvider {
    private static final BiMap<corgitaco.betterweather.api.season.Season.Key, Season> SEASON_MAPPER = HashBiMap.create();
    static {
        for (corgitaco.betterweather.api.season.Season.Key season : corgitaco.betterweather.api.season.Season.Key.values()) {
            SEASON_MAPPER.put(season, Season.valueOf(season.name()));
        }
    }

    @Override
    public Season getSeason(World world) {
        corgitaco.betterweather.api.season.Season season = corgitaco.betterweather.api.season.Season.getSeason(world);
        return season == null ? Season.SPRING : SEASON_MAPPER.get(season.getKey());
    }

    @Override
    public void setSeason(World world, Season season) {
        if (world.isClientSide) return;
        try {
            SeasonContext seasonContext = ((BetterWeatherWorldData)world).getSeasonContext();
            if (seasonContext != null)
                seasonContext.setSeason((ServerWorld) world, ((ServerWorld)world).players(), SEASON_MAPPER.inverse().get(season), seasonContext.getPhase());
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
