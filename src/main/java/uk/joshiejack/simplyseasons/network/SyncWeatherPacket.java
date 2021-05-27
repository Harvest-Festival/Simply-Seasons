package uk.joshiejack.simplyseasons.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import uk.joshiejack.penguinlib.network.PenguinPacket;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.simplyseasons.client.SSClient;
import uk.joshiejack.simplyseasons.world.weather.Weather;

@PenguinLoader.Packet(NetworkDirection.PLAY_TO_CLIENT)
public class SyncWeatherPacket extends PenguinPacket {
    private Weather weather;

    public SyncWeatherPacket() {}
    public SyncWeatherPacket(Weather weather) {
        this.weather = weather;
    }

    @Override
    public void encode(PacketBuffer to) {
        to.writeByte(weather.ordinal());
    }

    @Override
    public void decode(PacketBuffer from) {
        weather = Weather.values()[from.readByte()];
    }

    @Override
    public void handle(PlayerEntity player) {
        SSClient.INSTANCE.setWeather(weather);
    }
}
