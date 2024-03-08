package uk.joshiejack.simplyseasons.api;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SSeasonsAPI {
    /**
     *  Register localized season handlers here, ideally these will be checked by the season provider
     *  however it is possible that they won't be. To prevent special functions in certain dimensions.
     *  Its main purpose is to allow for certain locations to provide special seasons
     *  Use cases could include greenhouse buildings, or within range of certain blocks
     *
     *  The default providers for SimplySeasons, SereneSeasons and BetterWeather all make use of this
     *  So there is no need to call it separately
     */
    public static final List<ILocalizedSeasonProvider> LOCALIZED_SEASON_HANDLER = new ArrayList<>();

    /**
     * The API instance
     **/
    @SuppressWarnings("ConstantConditions")
    public static @NotNull SSeasonsAPI.Info instance() {
        return null;
    }

    public interface Info {
        /**
         * Each dimension will have its own interpretation of how the seasons function, so that
         * you can have an all winter dimension, or all summer etc. The provider can be reached through this api.
         * Not all worlds will have seasons, such as the nether and end in vanilla.
         *
         * @param level the level
         *              @return the season provider or empty if nothing is attached to this world
         */
        Optional<ISeasonProvider> getSeasonProvider(ResourceKey<Level> level);

        /**
         * Each world can have its own weather provider, that determines what weather happens
         * when. The weather will often be based on the season as well. This can mostly be ignored
         * As when setting the weather by default it will make sure to use the vanilla weather
         * @param level the level
         *              @return the weather provider or empty if nothing is attached to this world
         */
        Optional<IWeatherProvider> getWeatherProvider(ResourceKey<Level> level);

        /**
         *  Checks the current season at the specified position
         *  And returns if this block has been flagged as being able to grow in this season
         *
         *  @param level the level
         *  @param pos the position
         * @param state the state
         *             @return Whether this block can grow in this season
         */
        boolean canGrow(Level level, BlockPos pos, BlockState state);

        /**
         * Applies the out of season effect at the specified position
         *
         * @param level the level
         * @param pos the position
         *              @return whether the effect was applied
         */
        boolean applyOutOfSeasonEffect(Level level, BlockPos pos);
    }
}