package uk.joshiejack.simplyseasons.api;


import net.minecraft.world.level.Level;

/** The season provider is attached to a world as a capability **/
public interface ISeasonProvider extends ILocalizedSeasonProvider {
    /**
     * Return the current day of the season that it is
     * @param world the world
     * @return  the current day
     */
    int getDay(Level world);

    /**
     *  Grab the season for the entire world location.
     *
     * @param   world   the world
     * @return the current season
     */
    Season getSeason(Level world);

    /**
     * Called to change the season, on both server and client side
     * @param world     the world
     * @param season    the season
     */
    void setSeason(Level world, Season season);

    /**
     * Updates the current season when the time changes
     * @param world the world object
     */
    void recalculate(Level world);
}