package uk.joshiejack.simplyseasons.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Weather;

@Packet(PacketFlow.CLIENTBOUND)
public class WeatherChangedPacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("weather_changed");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }


    private final Weather weather;

    public WeatherChangedPacket(Weather weather) {
        this.weather = weather;
    }

    @SuppressWarnings("unused")
    public WeatherChangedPacket(FriendlyByteBuf from) {
        weather = Weather.values()[from.readByte()];
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeByte(weather.ordinal());
    }


    @Override
    public void handle(Player player) {
        SSeasonsAPI.instance().getWeatherProvider(player.level().dimension())
                .ifPresent((provider) -> provider.setWeather(player.level(), weather));
    }
}