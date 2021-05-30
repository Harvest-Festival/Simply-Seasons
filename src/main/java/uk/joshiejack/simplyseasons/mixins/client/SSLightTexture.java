package uk.joshiejack.simplyseasons.mixins.client;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
public class SSLightTexture {
    @Redirect(method = "updateLightTexture(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyDarken(F)F"))
    public float getSkyDarkness(ClientWorld clientWorld, float value) {
        return 0F;
    }
}
