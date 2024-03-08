package uk.joshiejack.simplyseasons.world;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import uk.joshiejack.penguinlib.event.NewDayEvent;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.network.DateChangedPacket;
import uk.joshiejack.simplyseasons.network.SeasonChangedPacket;
import uk.joshiejack.simplyseasons.network.ServerTypePacket;
import uk.joshiejack.simplyseasons.network.WeatherChangedPacket;

@SuppressWarnings("unused, ConstantConditions")
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class WorldUpdater {
    private static void sendUpdates(Player player) {
        if (!player.level().isClientSide) {
            SSeasonsAPI.instance().getSeasonProvider(player.level().dimension())
                    .ifPresent(provider -> PenguinNetwork.sendToClient((ServerPlayer) player, new SeasonChangedPacket(provider.getSeason(player.level()), false)));
            SSeasonsAPI.instance().getWeatherProvider(player.level().dimension())
                    .ifPresent(provider -> PenguinNetwork.sendToClient((ServerPlayer) player, new WeatherChangedPacket(provider.getWeather(player.level()))));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide)
            PenguinNetwork.sendToClient((ServerPlayer) event.getEntity(), new ServerTypePacket(event.getEntity().level().getServer().isSingleplayer()));
        sendUpdates(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        sendUpdates(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onNewDay(NewDayEvent event) { //Force an update when the day ticks over, so it happens instantly
        PenguinNetwork.sendToDimension(event.getLevel(), new DateChangedPacket());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) { //Periodically update the seasonal data for the client
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            if (event.level.getDayTime() % 30 == 2)
                SSeasonsAPI.instance().getSeasonProvider(event.level.dimension()).ifPresent(provider -> provider.recalculate(event.level));
            SSeasonsAPI.instance().getWeatherProvider(event.level.dimension()).ifPresent(provider -> provider.tick((ServerLevel) event.level));
        }
    }
}