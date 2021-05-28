package uk.joshiejack.simplyseasons.world.season;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helpers.minecraft.TimeHelper;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.network.DateChangedPacket;
import uk.joshiejack.simplyseasons.network.SeasonChangedPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractSeasonsProvider implements ISeasonsProvider {
    private final LazyOptional<AbstractSeasonsProvider> capability;

    public AbstractSeasonsProvider() {
        this.capability = LazyOptional.of(() -> this);
    }
    private Season previousSeason;
    private long previousElapsed;

    @Override
    public void recalculate(World world) {
        if (!world.isClientSide) {
            if (previousSeason != getSeason(world))
                PenguinNetwork.sendToDimension(new SeasonChangedPacket(), world.dimension());
            long elapsed = TimeHelper.getElapsedDays(world.getDayTime());
            if (elapsed != previousElapsed)
                PenguinNetwork.sendToDimension(new DateChangedPacket(), world.dimension());
            previousElapsed = elapsed;
            previousSeason = getSeason(world);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == SSeasonsAPI.SEASONS_CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

    //Stay down here out of my way!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @SuppressWarnings("rawtypes, unchecked")
    public static class Storage implements Capability.IStorage<ISeasonsProvider> {
        @Override
        public void readNBT(Capability<ISeasonsProvider> capability, ISeasonsProvider instance, Direction side, INBT nbt) {
            if (instance instanceof INBTSerializable)
                ((INBTSerializable) instance).deserializeNBT(nbt);
        }

        @Nullable
        @Override
        public INBT writeNBT(Capability<ISeasonsProvider> capability, ISeasonsProvider instance, Direction side) {
            return instance instanceof INBTSerializable ? ((INBTSerializable)instance).serializeNBT() : new CompoundNBT();
        }
    }
}
