package uk.joshiejack.simplyseasons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;

@Mixin(SSeasonsAPI.class)
public class SSApiInitializer {
    /**
     * @author joshiejack
     * @reason initialize the api
     */
    @Inject(method = "instance", at = @At("RETURN"), cancellable = true)
    private static void instance(CallbackInfoReturnable<SSeasonsAPI.Info> cir) {
        cir.setReturnValue(SimplySeasons.API);
    }
}