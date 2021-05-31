package uk.joshiejack.simplyseasons.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.ArrayList;
import java.util.List;

public class SSeasonsAPI {
    /**
     * Each dimension will have its own interpretation of how the seasons function, so that
     * you can have an all winter dimension, or all summer etc. These are world capabilities.
     * Not all worlds will have seasons, such as the nether and end in vanilla.
     */
    @CapabilityInject(ISeasonsProvider.class)
    public static final Capability<ISeasonsProvider> SEASONS_CAPABILITY = null;

    /** Each world can have its own weather provider, that determines what weather happens
     * when. The weather will often be based on the season as well.
     */
    @CapabilityInject(IWeatherProvider.class)
    public static final Capability<IWeatherProvider> WEATHER_CAPABILITY = null;

    /**
     *  Register localized season handlers here, ideally these will be checked by the season provider
     *  however it is possible that they won't be. To prevent special functions in certain dimensions.
     *  Its main purpose is to allow for certain locations to provide special seasons
     *  Use cases could include greenhouse buildings, or within range of certain blocks
     */
    public static final List<ILocalizedSeasonProvider> LOCALIZED_SEASON_HANDLER = new ArrayList<>();
}