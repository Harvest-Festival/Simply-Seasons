package uk.joshiejack.simplyseasons.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

@Mixin(WorldRenderer.class)
public abstract class SSWorldRenderer {
    private static final ResourceLocation BLIZZARD_LOCATION = new ResourceLocation(SimplySeasons.MODID, "textures/environment/blizzard.png");
    private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");

    @Redirect(method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    public float getTemperatureSnowAndRain(Biome biome, BlockPos pos) {
        return SeasonalWorlds.getTemperature(Minecraft.getInstance().level, biome, pos);
    }

    @Redirect(method = "tickRain(Lnet/minecraft/client/renderer/ActiveRenderInfo;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    public float getTemperatureTickRain(Biome biome, BlockPos pos) {
        return SeasonalWorlds.getTemperature(Minecraft.getInstance().level, biome, pos);
    }

    @Redirect(method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bind(Lnet/minecraft/util/ResourceLocation;)V"))
    public void getBlizzardTexture(TextureManager textureManager, ResourceLocation resource) {
        if (BetterWeatherPlugin.loaded || !resource.equals(SNOW_LOCATION)) return;
        if (Minecraft.getInstance().level.isThundering())
            textureManager.bind(BLIZZARD_LOCATION);
        else textureManager.bind(resource);
    }
}