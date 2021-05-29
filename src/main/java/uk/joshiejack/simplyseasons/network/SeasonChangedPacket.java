package uk.joshiejack.simplyseasons.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import uk.joshiejack.penguinlib.network.PenguinPacket;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

@PenguinLoader.Packet(NetworkDirection.PLAY_TO_CLIENT)
public class SeasonChangedPacket extends PenguinPacket {
    private Season season;

    public SeasonChangedPacket() { }
    public SeasonChangedPacket(Season season) {
        this.season = season;
    }

    @Override
    public void encode(PacketBuffer pb) {
        pb.writeByte(season.ordinal());
    }

    @Override
    public void decode(PacketBuffer pb) {
        season = Season.values()[pb.readByte()];
    }

    @Override
    public void handle(PlayerEntity player) {
        World world = player.level;
        world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY)
                .ifPresent(provider -> {
                    provider.setSeason(world, season);
                    Minecraft.getInstance().levelRenderer.allChanged();
                });
    }
}
