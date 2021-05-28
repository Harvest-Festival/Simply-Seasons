package uk.joshiejack.simplyseasons.api;

import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/** The seasons provider is attached to a world as a capability **/
public interface ISeasonsProvider extends ILocalizedSeasonProvider, ICapabilityProvider {
    /**
     *  Grab the season for the entire world location.
     *
     * @return the current season
     */
    Season getSeason(World world);

    /**
     * Updates the current season when the time changes
     * @param world the world object
     */
    void recalculate(World world);
}