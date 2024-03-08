package uk.joshiejack.simplyseasons.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.command.EnumArgument;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Weather;

import java.util.Locale;

public class SetWeatherCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("weather")
                .then(Commands.argument("weather", EnumArgument.enumArgument(Weather.class))
                        .executes(ctx -> {
                            Level world = ctx.getSource().getLevel();
                            IWeatherProvider provider = SSeasonsAPI.instance().getWeatherProvider(world.dimension()).orElse(null);
                            if (provider != null) {
                                Weather weather = ctx.getArgument("weather", Weather.class);
                                provider.setWeather(world, weather);
                                ctx.getSource().sendSuccess(() -> Component.translatable("command." +
                                        SimplySeasons.MODID + ".set_weather." + weather.name().toLowerCase(Locale.ROOT)), true);
                                return 1;
                            }

                            ctx.getSource().sendFailure(Component.translatable("command." + SimplySeasons.MODID + ".no_weather_world"));
                            return 0;
                        }));
    }
}
