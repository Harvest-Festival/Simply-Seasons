package uk.joshiejack.simplyseasons.mixins.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

@Mixin(ClientWorld.class)
public abstract class SSClientWorld {
    /**
     * Overwrites star brightness to make them brighter
     * @author  joshiejack
     */
    @Overwrite
    public float getStarBrightness(float p_228330_1_) {
        World world = (World) (Object) this;
        float f = world.getTimeOfDay(p_228330_1_);
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        LazyOptional<ISeasonsProvider> optional = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (optional.isPresent() && optional.resolve().get().getSeason(world) == Season.WINTER) {
            return f1 * f1 * 0.5F * 1.25F;
        } else return f1 * f1 * 0.5F;
    }
}
