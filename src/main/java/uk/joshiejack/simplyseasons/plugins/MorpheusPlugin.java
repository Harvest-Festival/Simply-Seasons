package uk.joshiejack.simplyseasons.plugins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.quetzi.morpheus.Morpheus;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.penguinlib.util.interfaces.IModPlugin;

import java.util.Objects;

@PenguinLoader("morpheus")
public class MorpheusPlugin implements IModPlugin {
    private long getTimeToSunrise(World world) {
        long dayLength = 24000;
        return dayLength - (world.getDayTime() % dayLength);
    }

    @Override
    public void setup() {
        try {
            Morpheus.register.unregisterHandler(World.OVERWORLD);
            Morpheus.register.registerHandler(() -> {
                World world = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(ServerWorld.OVERWORLD)).getWorldServer();
                ((ServerWorldInfo)world.getLevelData()).setDayTime(world.getLevelData().getDayTime() + getTimeToSunrise(world));
                world.players().stream().filter(LivingEntity::isSleeping).forEach(p -> p.stopSleepInBed(false, false));
            }, World.OVERWORLD);
        } catch (Exception ex) {
            PenguinLib.LOGGER.warn("Error encountered when trying to load the Morpheus Plugin from Simply Seasons.");
        }
    }
}