package uk.joshiejack.simplyseasons.mixins.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.renderer.SeasonalColorBlender;
import uk.joshiejack.simplyseasons.world.season.SeasonData;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class SSClientWorld extends World {
    protected SSClientWorld(ISpawnWorldInfo info, RegistryKey<World> key, DimensionType type,
                            Supplier<IProfiler> profiler, boolean clientSide, boolean debug, long biomeZoomSeed) {
        super(info, key, type, profiler, clientSide, debug, biomeZoomSeed);
    }

    /**
     * Overwrites star brightness to make them brighter
     *
     * @author joshiejack
     */
    @Overwrite
    public float getStarBrightness(float p_228330_1_) {
        float f = this.getTimeOfDay(p_228330_1_);
        float f1 = 1.0F - (MathHelper.cos(f * ((float) Math.PI * 2F)) * 2.0F + 0.25F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        LazyOptional<ISeasonsProvider> optional = this.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (optional.isPresent() && optional.resolve().get().getSeason(this) == Season.WINTER) {
            return f1 * f1 * 0.5F * 1.25F;
        } else return f1 * f1 * 0.5F;
    }

    /**
     * Seasonal sky colouring
     *
     * @author joshiejack
     */
    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getSkyColor()I"))
    public int getSkyColor(Biome biome) {
        LazyOptional<ISeasonsProvider> provider = getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        return provider.isPresent() ? SeasonalColorBlender.getBlendedColor(biome.getSkyColor(),
                SeasonData.get(provider.resolve().get().getSeason(this)).sky, 10) : biome.getSkyColor();
    }
}