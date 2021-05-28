package uk.joshiejack.simplyseasons.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.events.NewDayEvent;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.network.DateChangedPacket;
import uk.joshiejack.simplyseasons.network.SeasonChangedPacket;

@SuppressWarnings("unused, ConstantConditions")
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class WorldUpdater {
    @SubscribeEvent
    public static void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide && event.getEntity() instanceof PlayerEntity) {
            PenguinNetwork.sendToClient(new DateChangedPacket(), (ServerPlayerEntity) event.getEntity());
            event.getWorld().getCapability(SSeasonsAPI.SEASONS_CAPABILITY)
                    .ifPresent(provider -> PenguinNetwork.sendToClient(new SeasonChangedPacket(), (ServerPlayerEntity) event.getEntity()));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onNewDay(NewDayEvent event) { //Force an update when the day ticks over, so it happens instantly
        PenguinNetwork.sendToDimension(new DateChangedPacket(), event.getWorld().dimension());
        PenguinNetwork.sendToDimension(new SeasonChangedPacket(), event.getWorld().dimension());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) { //Periodically update the seasonal data for the client
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END && event.world.getDayTime() % 60 == 0)
            event.world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY).ifPresent(provider -> provider.recalculate(event.world));
    }
}