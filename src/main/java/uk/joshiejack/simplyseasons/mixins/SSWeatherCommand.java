package uk.joshiejack.simplyseasons.mixins;

import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.WeatherCommand;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;

@Mixin(WeatherCommand.class)
public class SSWeatherCommand {
    @Inject(method = "setRain", at = @At("HEAD"), cancellable = true)
    private static void setRainOverride(CommandSource source, int time, CallbackInfoReturnable<Integer> ci) {
        if (BetterWeatherPlugin.loaded || !source.getLevel().getCapability(SSeasonsAPI.WEATHER_CAPABILITY).isPresent()) return;
        source.sendFailure(new TranslationTextComponent("command." + SimplySeasons.MODID + ".weather.disabled"));
        ci.setReturnValue(0);
    }

    @Inject(method = "setClear", at = @At("HEAD"), cancellable = true)
    private static void setClearOverride(CommandSource source, int time, CallbackInfoReturnable<Integer> ci) {
        if (BetterWeatherPlugin.loaded || !source.getLevel().getCapability(SSeasonsAPI.WEATHER_CAPABILITY).isPresent()) return;
        source.sendFailure(new TranslationTextComponent("command." + SimplySeasons.MODID + ".weather.disabled"));
        ci.setReturnValue(0);
    }

    @Inject(method = "setThunder", at = @At("HEAD"), cancellable = true)
    private static void setThunderOverride(CommandSource source, int time, CallbackInfoReturnable<Integer> ci) {
        if (BetterWeatherPlugin.loaded || !source.getLevel().getCapability(SSeasonsAPI.WEATHER_CAPABILITY).isPresent()) return;
        source.sendFailure(new TranslationTextComponent("command." + SimplySeasons.MODID + ".weather.disabled"));
        ci.setReturnValue(0);
    }
}
