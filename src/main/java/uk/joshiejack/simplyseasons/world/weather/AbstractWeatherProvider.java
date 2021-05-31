package uk.joshiejack.simplyseasons.world.weather;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.network.WeatherChangedPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractWeatherProvider implements IWeatherProvider, INBTSerializable<CompoundNBT> {
    private final LazyOptional<AbstractWeatherProvider> capability;
    protected Weather current;
    protected Weather forecast;
    protected int updateFrequency;
    protected int changeChance;

    public AbstractWeatherProvider(int frequency, int chance) {
        this.updateFrequency = frequency;
        this.changeChance = chance;
        this.capability = LazyOptional.of(() -> this);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == SSeasonsAPI.WEATHER_CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

    @Override
    public Weather getWeather(World world) {
        if (current == null) {
            this.current = getRandom(world);
            this.forecast = getRandom(world);
        }

        return current;
    }

    protected abstract Weather getRandom(World world);

    @Override
    public void setWeather(World world, Weather weather) {
        this.current = weather;
        if (!world.isClientSide)
            PenguinNetwork.sendToDimension(new WeatherChangedPacket(current), world.dimension());
    }

    @Override
    public void tick(ServerWorld world) {
        if (world.getDayTime() % updateFrequency == 1 || (changeChance > 1 && world.random.nextInt(changeChance) == 0)) {
            setWeather(world, forecast); //Change the current weather to the forecast one
            forecast = getRandom(world); //Grab a new forecasted weather
            if (current == Weather.CLEAR || current == Weather.FOGGY)
                world.setWeatherParameters(Integer.MAX_VALUE, 0, false, false);
            else if (current == Weather.RAIN)
                world.setWeatherParameters(0, Integer.MAX_VALUE, true, false);
            else if (current == Weather.STORM)
                world.setWeatherParameters(0, Integer.MAX_VALUE, true, true);
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Current")) {
            current = Weather.values()[nbt.getByte("Current")];
            forecast = Weather.values()[nbt.getByte("Forecast")];
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        if (current != null) {
            nbt.putByte("Current", (byte) current.ordinal());
            nbt.putByte("Forecast", (byte) forecast.ordinal());
        }

        return nbt;
    }

    //Stay down here out of my way!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @SuppressWarnings("rawtypes, unchecked")
    public static class Storage implements Capability.IStorage<IWeatherProvider> {
        @Override
        public void readNBT(Capability<IWeatherProvider> capability, IWeatherProvider instance, Direction side, INBT nbt) {
            if (instance instanceof INBTSerializable)
                ((INBTSerializable) instance).deserializeNBT(nbt);
        }

        @Nullable
        @Override
        public INBT writeNBT(Capability<IWeatherProvider> capability, IWeatherProvider instance, Direction side) {
            return instance instanceof INBTSerializable ? ((INBTSerializable)instance).serializeNBT() : new CompoundNBT();
        }
    }
}
