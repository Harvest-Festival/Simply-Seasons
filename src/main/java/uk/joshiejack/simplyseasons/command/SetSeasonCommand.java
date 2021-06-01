package uk.joshiejack.simplyseasons.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.server.command.EnumArgument;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import java.util.Locale;

public class SetSeasonCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("season")
                .then(Commands.argument("season", EnumArgument.enumArgument(CommandSeason.class))
                        .executes(ctx -> {
                            World world = ctx.getSource().getLevel();
                            LazyOptional<ISeasonsProvider> provider = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
                            if (provider.isPresent()) {
                                Season season = Season.valueOf(ctx.getArgument("season", CommandSeason.class).name());
                                provider.resolve().get().setSeason(world, season);
                                ctx.getSource().sendSuccess(new TranslationTextComponent("command." +
                                        SimplySeasons.MODID + ".set_season." + season.name().toLowerCase(Locale.ROOT)), true);
                                return 1;
                            }

                            ctx.getSource().sendFailure(new TranslationTextComponent("command." + SimplySeasons.MODID + ".no_seasons_world"));
                            return 0;
                        }));
    }

    public enum CommandSeason {
        SPRING, SUMMER, AUTUMN, WINTER
    }
}
