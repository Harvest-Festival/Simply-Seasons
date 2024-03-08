package uk.joshiejack.simplyseasons.mixin;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.WeatherCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;

@Mixin(value = WeatherCommand.class, priority = 999)
public class SSWeatherCommand {
    @Inject(method = "setRain", at = @At("HEAD"), cancellable = true)
    private static void setRainOverride(CommandSourceStack source, int time, CallbackInfoReturnable<Integer> ci) {
        if (!SSeasonsAPI.instance().getWeatherProvider(source.getLevel().dimension()).isPresent()) return;
        source.sendFailure(Component.translatable("command." + SimplySeasons.MODID + ".weather.disabled"));
        ci.setReturnValue(0);
    }

    @Inject(method = "setClear", at = @At("HEAD"), cancellable = true)
    private static void setClearOverride(CommandSourceStack source, int time, CallbackInfoReturnable<Integer> ci) {
        if (!SSeasonsAPI.instance().getWeatherProvider(source.getLevel().dimension()).isPresent()) return;
        source.sendFailure(Component.translatable("command." + SimplySeasons.MODID + ".weather.disabled"));
        ci.setReturnValue(0);
    }

    @Inject(method = "setThunder", at = @At("HEAD"), cancellable = true)
    private static void setThunderOverride(CommandSourceStack source, int time, CallbackInfoReturnable<Integer> ci) {
        if (!SSeasonsAPI.instance().getWeatherProvider(source.getLevel().dimension()).isPresent()) return;
        source.sendFailure(Component.translatable("command." + SimplySeasons.MODID + ".weather.disabled"));
        ci.setReturnValue(0);
    }
}
