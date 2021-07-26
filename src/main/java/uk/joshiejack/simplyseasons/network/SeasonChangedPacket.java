package uk.joshiejack.simplyseasons.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import uk.joshiejack.penguinlib.network.PenguinPacket;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClient;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;
import uk.joshiejack.simplyseasons.plugins.SereneSeasonsPlugin;

@PenguinLoader.Packet(NetworkDirection.PLAY_TO_CLIENT)
public class SeasonChangedPacket extends PenguinPacket {
    private Season season;
    private boolean refresh;

    public SeasonChangedPacket() { }
    public SeasonChangedPacket(Season season, boolean refresh) {
        this.season = season;
        this.refresh = refresh;
    }

    @Override
    public void encode(PacketBuffer pb) {
        pb.writeByte(season.ordinal());
        pb.writeBoolean(refresh);
    }

    @Override
    public void decode(PacketBuffer pb) {
        season = Season.values()[pb.readByte()];
        refresh = pb.readBoolean();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleClientPacket() {
        SSClient.HUD.get().recalculateDate(Minecraft.getInstance().level);
        World world = Minecraft.getInstance().level;
        world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY)
                .ifPresent(provider -> {
                    provider.setSeason(world, season);
                    if (refresh && !SereneSeasonsPlugin.loaded && !BetterWeatherPlugin.loaded)
                        Minecraft.getInstance().levelRenderer.allChanged();
                });
    }
}
