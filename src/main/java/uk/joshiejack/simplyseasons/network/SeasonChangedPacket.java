package uk.joshiejack.simplyseasons.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import uk.joshiejack.penguinlib.network.PenguinPacket;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;

@PenguinLoader.Packet(NetworkDirection.PLAY_TO_CLIENT)
public class SeasonChangedPacket extends PenguinPacket {
    public SeasonChangedPacket() {
    }

    @Override
    public void handle(PlayerEntity player) {
        World world = player.level;
        world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY)
                .ifPresent(provider -> {
                    provider.recalculate(world);
                    Minecraft.getInstance().levelRenderer.allChanged();
                });
    }
}
