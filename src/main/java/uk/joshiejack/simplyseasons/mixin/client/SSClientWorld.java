package uk.joshiejack.simplyseasons.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.renderer.SeasonalColorBlender;
import uk.joshiejack.simplyseasons.world.season.SeasonData;

import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions, unused")
@Mixin(value = ClientLevel.class, priority = 999)
public abstract class SSClientWorld extends Level {
    protected SSClientWorld(WritableLevelData data, ResourceKey<Level> key, RegistryAccess access, Holder<DimensionType> dim, Supplier<ProfilerFiller> profiler, boolean b1, boolean b2, long l, int p) {
        super(data, key, access, dim, profiler, b1, b2, l, p);
    }

    /**
     * @reason Overwrites star brightness to make them brighter
     * @author joshiejack
     */
    @Inject(method = "getStarBrightness", at = @At("RETURN"), cancellable = true)
    public void getStarBrightness(float p_104812_, CallbackInfoReturnable<Float> cir) {
        float initial = cir.getReturnValue();
        ISeasonProvider optional = SSeasonsAPI.instance().getSeasonProvider(dimension()).orElse(null);
        if (optional != null && optional.getSeason(this) == Season.WINTER) {
            cir.setReturnValue(initial * 1.25F);
        } else cir.setReturnValue(initial);
    }

    /**
     * Seasonal sky colouring
     *
     * @author joshiejack
     */
    @Redirect(method = "lambda$getSkyColor$12(Lnet/minecraft/world/level/biome/BiomeManager;III)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "INVOKE", target = "net/minecraft/world/level/biome/Biome.getSkyColor ()I"))
    private static int getSkyColor(Biome biome) {
        Level level = Minecraft.getInstance().level;
        ISeasonProvider provider = SSeasonsAPI.instance().getSeasonProvider(level.dimension()).orElse(null);
        return provider != null ? SeasonalColorBlender.getBlendedColor(biome.getSkyColor(),
                SeasonData.get(provider.getSeason(level)).sky(), 10) : biome.getSkyColor();
    }
}