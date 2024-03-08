package uk.joshiejack.simplyseasons.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.simplyseasons.client.SSClient;

@Packet(PacketFlow.CLIENTBOUND)
public class DateChangedPacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("date_changed");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public DateChangedPacket() {}

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleClient() {
        SSClient.HUD.get().recalculateDate(Minecraft.getInstance().level);
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) { }
}