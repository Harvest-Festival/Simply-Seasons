package uk.joshiejack.simplyseasons.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.simplyseasons.SimplySeasons;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SSCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSource>literal(SimplySeasons.MODID)
                        .requires(cs -> cs.hasPermission(2))
                        .then(SetSeasonCommand.register())
                        .then(SetWeatherCommand.register())
        );
    }
}

