package uk.joshiejack.simplyseasons.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SSCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal(SimplySeasons.MODID)
                        .requires(cs -> cs.hasPermission(2))
                        .then(SetSeasonCommand.register())
                        .then(SetWeatherCommand.register())
        );
    }
}

