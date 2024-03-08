package uk.joshiejack.simplyseasons.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

public interface ILocalizedSeasonProvider {
    /**
     *  Grab the seasons at the current location. It is possible for the location
     *  to be considered multiple seasons. And the intended functionality is that
     *  if something 'requires' the summer and it is in the set it should function.
     *  This is to allow for something like specific location based greenhouses
     *  or other such features.
     *
     * @param world     The world we are checking for the season in
     * @param pos       The block pos to check
     * @return a set of seasons at that location
     */
    Set<Season> getSeasonsAt(Level world, BlockPos pos);
}