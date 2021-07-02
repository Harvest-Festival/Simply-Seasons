package uk.joshiejack.simplyseasons.mixins;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.extensions.IForgeWorld;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.joshiejack.penguinlib.util.helpers.minecraft.TimeHelper;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.world.season.SeasonData;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

@Mixin(value = World.class, priority = 999)
public abstract class SSWorld implements IWorld, AutoCloseable, IForgeWorld {
    public World asWorld() {
        return (World) (Object) this;
    }

    /**
     * Overrides the sunrise and sunset based on the time of day
     *
     * @author joshiejack
     */
    public float getTimeOfDay(float partialTicks) {
        LazyOptional<ISeasonProvider> optional = getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (optional.isPresent()) {
            SeasonData data = SeasonData.get(optional.resolve().get().getSeason(asWorld()));
            long time = TimeHelper.getTimeOfDay(asWorld().getDayTime());
            if (time >= data.sunrise && time < data.sunset) {
                long daytime = (data.sunset - data.sunrise);
                return -((((time - data.sunrise) * -0.5f) / daytime) - 0.75f);
            } else {
                if (time < data.sunrise) time += 24000L; //Adjust the time so that we're the day after
                long daytime = ((24000L + data.sunrise) - data.sunset);
                return -((((time - data.sunset) * -0.5f) / daytime) - 0.25f);
            }
        }

        return this.dimensionType().timeOfDay(this.dayTime());
    }

    @Redirect(method = "isRainingAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    public float getTemperature(Biome biome, BlockPos pos) {
        return SeasonalWorlds.getTemperature((World) (Object) this, biome, pos);
    }
}