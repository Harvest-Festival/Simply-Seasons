package uk.joshiejack.simplyseasons.world.weather;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;

public class SeasonalWeatherProvider extends AbstractWeatherProvider implements INBTSerializable<CompoundNBT> {
    public SeasonalWeatherProvider(int frequency, int chance) {
        super(frequency, chance);
    }

    @Override
    protected Weather getRandom(World world) {
        LazyOptional<ISeasonsProvider> provider = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (provider.isPresent())
            return WeatheredWorlds.getRandomWeatherForSeason(provider.resolve().get().getSeason(world), world.random);
        else
            return WeatheredWorlds.getRandomWeatherForSeason(null, world.random);
    }
}
