package uk.joshiejack.simplyseasons.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.client.SSClientConfig;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

@Mixin(value = LevelRenderer.class, priority = 999)
public abstract class SSLevelRenderer {
    @Unique
    private static final ResourceLocation BLIZZARD_LOCATION = new ResourceLocation(SimplySeasons.MODID, "textures/environment/blizzard.png");
    @Unique
    private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");

    @Redirect(method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", at = @At(value = "INVOKE", target = "net/minecraft/world/level/biome/Biome.getPrecipitationAt (Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    public Biome.Precipitation getTemperatureSnowAndRain(Biome biome, BlockPos pos) {
        return SeasonalWorlds.getPrecipitationAt(Minecraft.getInstance().level, biome, pos);
    }

    @Redirect(method = "tickRain(Lnet/minecraft/client/Camera;)V", at = @At(value = "INVOKE", target = "net/minecraft/world/level/biome/Biome.getPrecipitationAt (Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    public Biome.Precipitation getPrecipitationSS(Biome biome, BlockPos pos) {
        return SeasonalWorlds.getPrecipitationAt(Minecraft.getInstance().level, biome, pos);
    }

    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", at = @At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.setShaderTexture (ILnet/minecraft/resources/ResourceLocation;)V"))
    public void getBlizzardTexture(int p_157457_, ResourceLocation resource) {
        if (!resource.equals(SNOW_LOCATION) || !Minecraft.getInstance().level.isThundering() || !SSClientConfig.enableBlizzardTexture.get())
            RenderSystem.setShaderTexture(0, resource);
        else
            RenderSystem.setShaderTexture(0, BLIZZARD_LOCATION);
    }
}