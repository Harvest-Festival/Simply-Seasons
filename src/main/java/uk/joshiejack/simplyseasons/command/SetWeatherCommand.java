package uk.joshiejack.simplyseasons.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.server.command.EnumArgument;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.world.weather.Weather;

import java.util.Locale;

public class SetWeatherCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("weather")
                .then(Commands.argument("weather", EnumArgument.enumArgument(Weather.class))
                        .executes(ctx -> {
                            World world = ctx.getSource().getLevel();
                            LazyOptional<IWeatherProvider> provider = world.getCapability(SSeasonsAPI.WEATHER_CAPABILITY);
                            if (provider.isPresent()) {
                                Weather weather = ctx.getArgument("weather", Weather.class);
                                provider.resolve().get().setWeather(world, weather);
                                ctx.getSource().sendSuccess(new TranslationTextComponent("command." +
                                        SimplySeasons.MODID + ".set_weather." + weather.name().toLowerCase(Locale.ROOT)), true);
                                return 1;
                            }

                            ctx.getSource().sendFailure(new TranslationTextComponent("command." + SimplySeasons.MODID + ".no_weather_world"));
                            return 0;
                        }));
    }
}
