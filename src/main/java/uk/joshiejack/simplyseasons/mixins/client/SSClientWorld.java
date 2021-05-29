package uk.joshiejack.simplyseasons.mixins.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow
    private int skyFlashTime;

    /**
     * Seasonal sky colouring
     *
     * @author joshiejack
     */
    @Overwrite
    public Vector3d getSkyColor(BlockPos p_228318_1_, float p_228318_2_) {
        float f = this.getTimeOfDay(p_228318_2_);
        float f1 = MathHelper.cos(f * ((float) Math.PI * 2F)) * 2.0F + 0.5F;
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        Biome biome = this.getBiome(p_228318_1_);
        LazyOptional<ISeasonsProvider> provider = getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        int i = provider.isPresent() ? SeasonalColorBlender.getBlendedColor(biome.getSkyColor(),
                SeasonData.DATA.get(provider.resolve().get().getSeason(this)).sky, 10) : biome.getSkyColor();
        float f2 = (float) (i >> 16 & 255) / 255.0F;
        float f3 = (float) (i >> 8 & 255) / 255.0F;
        float f4 = (float) (i & 255) / 255.0F;
        f2 = f2 * f1;
        f3 = f3 * f1;
        f4 = f4 * f1;
        float f5 = this.getRainLevel(p_228318_2_);
        if (f5 > 0.0F) {
            float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
            float f7 = 1.0F - f5 * 0.75F;
            f2 = f2 * f7 + f6 * (1.0F - f7);
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
        }

        float f9 = this.getThunderLevel(p_228318_2_);
        if (f9 > 0.0F) {
            float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            float f8 = 1.0F - f9 * 0.75F;
            f2 = f2 * f8 + f10 * (1.0F - f8);
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
        }

        if (this.skyFlashTime > 0) {
            float f11 = (float) this.skyFlashTime - p_228318_2_;
            if (f11 > 1.0F) {
                f11 = 1.0F;
            }

            f11 = f11 * 0.45F;
            f2 = f2 * (1.0F - f11) + 0.8F * f11;
            f3 = f3 * (1.0F - f11) + 0.8F * f11;
            f4 = f4 * (1.0F - f11) + 1.0F * f11;
        }

        return new Vector3d(f2, f3, f4);
    }
}
