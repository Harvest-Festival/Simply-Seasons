package uk.joshiejack.simplyseasons.mixins;

import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeWorld;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import uk.joshiejack.penguinlib.util.helpers.minecraft.TimeHelper;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.world.season.SeasonData;

@Mixin(World.class)
public abstract class SSWorld implements IWorld, AutoCloseable, IForgeWorld {
    /**
     * Overrides the sunrise and sunset based on the time of day
     *
     * @author joshiejack
     */
    public float getTimeOfDay(float partialTicks) {
        LazyOptional<ISeasonsProvider> optional = getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (optional.isPresent()) {
            SeasonData data = SeasonData.DATA.get(optional.resolve().get().getSeason((World) (Object) this));
            long time = TimeHelper.getTimeOfDay(((World) (Object) this).getDayTime());
            if (time >= data.sunrise && time < data.sunset) {
                long daytime = (data.sunset - data.sunrise);
                return (((time - data.sunrise) * -0.5f) / daytime) - 0.75f;
            } else {
                if (time < data.sunrise) time += 24000L; //Adjust the time so that we're the day after
                long daytime = ((24000L + data.sunrise) - data.sunset);
                return (((time - data.sunset) * -0.5f) / daytime) - 0.25f;
            }
        }

        return this.dimensionType().timeOfDay(this.dayTime());
    }
}