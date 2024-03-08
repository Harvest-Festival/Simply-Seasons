package uk.joshiejack.simplyseasons.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.simplyseasons.world.CalendarDate;

@Packet(PacketFlow.CLIENTBOUND)
public class ServerTypePacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("server_type");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }


    private final boolean isSinglePlayer;

    public ServerTypePacket(boolean isSinglePlayer) {
        this.isSinglePlayer = isSinglePlayer;
    }
    @SuppressWarnings("unused")
    public ServerTypePacket(FriendlyByteBuf pb) {
        isSinglePlayer = pb.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf pb) {
        pb.writeBoolean(isSinglePlayer);
    }

    @Override
    public void handle(Player player) {
        CalendarDate.isSinglePlayer = isSinglePlayer;
    }
}
