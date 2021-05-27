package uk.joshiejack.simplyseasons.api;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

/** The seasons provider is attached to a world as a capability **/
public interface ISeasonsProvider extends ILocalizedSeasonProvider, ICapabilityProvider, INBTSerializable<CompoundNBT> {
    /**
     *  Grab the season for the entire world location.
     *
     * @return the current season
     */
    Season getSeason();

    /**
     * Updates the current season when the time changes
     * @param world the world object
     */
    void recalculate(ServerWorld world);
}