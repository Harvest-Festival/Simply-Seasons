package uk.joshiejack.simplyseasons.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import uk.joshiejack.penguinlib.network.PenguinPacket;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.simplyseasons.client.SSClient;
import uk.joshiejack.simplyseasons.world.CalendarDate;

import java.time.DayOfWeek;

@PenguinLoader.Packet(NetworkDirection.PLAY_TO_CLIENT)
public class SyncDatePacket extends PenguinPacket {
    private CalendarDate date;

    public SyncDatePacket() {}
    public SyncDatePacket(CalendarDate date) {
        this.date = date;
    }

    @Override
    public void encode(PacketBuffer to) {
        to.writeByte(date.getWeekday().ordinal());
        to.writeShort(date.getDay());
        to.writeShort(date.getYear());
    }

    @Override
    public void decode(PacketBuffer from) {
        date = new CalendarDate(DayOfWeek.values()[from.readByte()], from.readShort(), from.readShort());
    }

    @Override
    public void handle(PlayerEntity player) {
        SSClient.INSTANCE.setDate(date); //Client side
    }
}