package uk.joshiejack.simplyseasons.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.events.NewDayEvent;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.network.*;

@SuppressWarnings("unused, ConstantConditions")
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class WorldUpdater {
    private static void sendUpdates(PlayerEntity player) {
        if (!player.level.isClientSide) {
            player.level.getCapability(SSeasonsAPI.SEASONS_CAPABILITY)
                    .ifPresent(provider -> PenguinNetwork.sendToClient(new SeasonChangedPacket(provider.getSeason(player.level), false), (ServerPlayerEntity) player));
            player.level.getCapability(SSeasonsAPI.WEATHER_CAPABILITY)
                    .ifPresent(provider -> PenguinNetwork.sendToClient(new WeatherChangedPacket(provider.getWeather(player.level)), (ServerPlayerEntity) player));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide)
            PenguinNetwork.sendToClient(new ServerTypePacket(event.getPlayer().level.getServer().isSingleplayer()), (ServerPlayerEntity) event.getPlayer());
        sendUpdates(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        sendUpdates(event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onNewDay(NewDayEvent event) { //Force an update when the day ticks over, so it happens instantly
        //PenguinNetwork.sendToDimension(new DateChangedPacket(), event.getWorld().dimension());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) { //Periodically update the seasonal data for the client
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            if (event.world.getDayTime() % 30 == 2)
                event.world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY).ifPresent(provider -> provider.recalculate(event.world));
            event.world.getCapability(SSeasonsAPI.WEATHER_CAPABILITY).ifPresent(provider -> provider.tick((ServerWorld) event.world));
        }
    }
}