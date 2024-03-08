package uk.joshiejack.simplyseasons.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClient;
import uk.joshiejack.simplyseasons.plugin.SereneSeasonsPlugin;

@Packet(PacketFlow.CLIENTBOUND)
public class SeasonChangedPacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("season_changed");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    private Season season;
    private boolean refresh;

    public SeasonChangedPacket() { }
    public SeasonChangedPacket(Season season, boolean refresh) {
        this.season = season;
        this.refresh = refresh;
    }

    public SeasonChangedPacket(FriendlyByteBuf pb) {
        season = Season.values()[pb.readByte()];
        refresh = pb.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf pb) {
        pb.writeByte(season.ordinal());
        pb.writeBoolean(refresh);
    }

    @SuppressWarnings("ConstantConditions")
    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleClient() {
        SSClient.HUD.get().recalculateDate(Minecraft.getInstance().level);
        Level world = Minecraft.getInstance().level;
        SSeasonsAPI.instance().getSeasonProvider(world.dimension())
                .ifPresent(provider -> {
                    provider.setSeason(world, season);
                    if (refresh && !SereneSeasonsPlugin.loaded)
                        Minecraft.getInstance().levelRenderer.allChanged();
                });
    }
}
