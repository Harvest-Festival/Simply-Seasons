package uk.joshiejack.simplyseasons.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import uk.joshiejack.penguinlib.network.PenguinPacket;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.simplyseasons.world.CalendarDate;

@PenguinLoader.Packet(NetworkDirection.PLAY_TO_CLIENT)
public class ServerTypePacket extends PenguinPacket {
    private boolean isSinglePlayer;

    public ServerTypePacket() { }
    public ServerTypePacket(boolean isSinglePlayer) {
        this.isSinglePlayer = isSinglePlayer;
    }

    @Override
    public void encode(PacketBuffer pb) {
        pb.writeBoolean(isSinglePlayer);
    }

    @Override
    public void decode(PacketBuffer pb) {
        isSinglePlayer = pb.readBoolean();
    }

    @Override
    public void handle(PlayerEntity player) {
        CalendarDate.isSinglePlayer = isSinglePlayer;
    }
}
