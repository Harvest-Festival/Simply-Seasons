package uk.joshiejack.simplyseasons.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.command.EnumArgument;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import java.util.Locale;

public class SetSeasonCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("season")
                .then(Commands.argument("season", EnumArgument.enumArgument(CommandSeason.class))
                        .executes(ctx -> {
                            Level world = ctx.getSource().getLevel();
                            ISeasonProvider provider = SSeasonsAPI.instance().getSeasonProvider(world.dimension()).orElse(null);
                            if (provider != null) {
                                Season season = Season.valueOf(ctx.getArgument("season", CommandSeason.class).name());
                                provider.setSeason(world, season);
                                ctx.getSource().sendSuccess(() -> Component.translatable("command." +
                                        SimplySeasons.MODID + ".set_season." + season.name().toLowerCase(Locale.ROOT)), true);
                                return 1;
                            }

                            ctx.getSource().sendFailure(Component.translatable("command." + SimplySeasons.MODID + ".no_seasons_world"));
                            return 0;
                        }));
    }

    public enum CommandSeason {
        SPRING, SUMMER, AUTUMN, WINTER
    }
}
