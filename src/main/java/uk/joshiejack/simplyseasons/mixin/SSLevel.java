package uk.joshiejack.simplyseasons.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.world.season.SeasonData;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

import java.util.Optional;

@SuppressWarnings("ConstantConditions, resource")
@Mixin(value = Level.class, priority = 999)
public abstract class SSLevel implements LevelAccessor, AutoCloseable, ILevelExtension {
    @Unique
    public Level simplySeasons$asWorld() {
        return (Level) (Object) this;
    }

    /**
     * Overrides the sunrise and sunset based on the time of day
     *
     * @author joshiejack
     */
    public float getTimeOfDay(float partialTicks) {
        Optional<ISeasonProvider> optional = SSeasonsAPI.instance().getSeasonProvider(simplySeasons$asWorld().dimension());
        if (optional.isPresent()) {
            SeasonData data = SeasonData.get(optional.get().getSeason(simplySeasons$asWorld()));
            long time = TimeHelper.getTimeOfDay(simplySeasons$asWorld().getDayTime());
            if (time >= data.sunrise() && time < data.sunset()) {
                long daytime = (data.sunset() - data.sunrise());
                return -((((time - data.sunrise()) * -0.5f) / daytime) - 0.75f);
            } else {
                if (time < data.sunrise()) time += 24000L; //Adjust the time so that we're the day after
                long daytime = ((24000L + data.sunrise()) - data.sunset());
                return -((((time - data.sunset()) * -0.5f) / daytime) - 0.25f);
            }
        }

        return this.dimensionType().timeOfDay(this.dayTime());
    }

    //@Redirect(method = "isRainingAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/core/BlockPos;)F"))
    @Redirect(method = "isRainingAt", at = @At(value = "INVOKE", target = "net/minecraft/world/level/biome/Biome.getPrecipitationAt (Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    public Biome.Precipitation getPrecipitation(Biome biome, BlockPos pos) {
        return SeasonalWorlds.getPrecipitationAt(this, biome, pos);
    }
}