package uk.joshiejack.simplyseasons.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.events.NewDayEvent;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.network.SyncDatePacket;
import uk.joshiejack.simplyseasons.network.SyncSeasonPacket;
import uk.joshiejack.simplyseasons.world.date.CalendarDate;

@SuppressWarnings("unused, ConstantConditions")
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class WorldUpdater {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide)
            PenguinNetwork.sendToClient(new SyncDatePacket(CalendarDate.getFromTime(event.getPlayer().level.getDayTime())),
                    (ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide && event.getEntity() instanceof PlayerEntity) {
            LazyOptional<ISeasonsProvider> seasonsProvider = event.getWorld().getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
            if (seasonsProvider.isPresent()) {
                PenguinNetwork.sendToClient(new SyncSeasonPacket(seasonsProvider.resolve().get().getSeason()), (ServerPlayerEntity) event.getEntity());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onNewDay(NewDayEvent event) {
        LazyOptional<ISeasonsProvider> seasonsProvider = event.getWorld().getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (seasonsProvider.isPresent()) {
            seasonsProvider.resolve().get().recalculate(event.getWorld());
            PenguinNetwork.sendToDimension(new SyncSeasonPacket(seasonsProvider.resolve().get().getSeason()), event.getWorld().dimension());
        }

        PenguinNetwork.sendToDimension(new SyncDatePacket(CalendarDate.getFromTime(event.getWorld().getDayTime())), event.getWorld().dimension());
    }

    //TODO: Mixin for when the time is changed by command, to recalculate the season and the date
}