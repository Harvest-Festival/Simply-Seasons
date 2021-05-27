package uk.joshiejack.simplyseasons.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import uk.joshiejack.penguinlib.network.PenguinPacket;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClient;

@PenguinLoader.Packet(NetworkDirection.PLAY_TO_CLIENT)
public class SyncSeasonPacket extends PenguinPacket {
    private Season season;

    public SyncSeasonPacket() {}
    public SyncSeasonPacket(Season season) {
        this.season = season;
    }

    @Override
    public void encode(PacketBuffer to) {
        to.writeByte(season.ordinal());
    }

    @Override
    public void decode(PacketBuffer from) {
        season = Season.values()[from.readByte()];
    }

    @Override
    public void handle(PlayerEntity player) {
        SSClient.INSTANCE.setSeason(season);
    }
}
