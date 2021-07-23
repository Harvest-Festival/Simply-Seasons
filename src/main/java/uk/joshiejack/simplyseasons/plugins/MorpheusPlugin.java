package uk.joshiejack.simplyseasons.plugins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.quetzi.morpheus.Morpheus;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.penguinlib.util.interfaces.IModPlugin;

import java.util.Objects;

@PenguinLoader("morpheus")
public class MorpheusPlugin implements IModPlugin {
    private long getDayTime(ServerWorld world) {
        long l = world.getDayTime() + 24000L;
        return ForgeEventFactory.onSleepFinished(world, l - l % 24000L, world.getDayTime());
    }

    @Override
    public void setup() {
        try {
            Morpheus.register.registerHandler(() -> {
                ServerWorld world = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(ServerWorld.OVERWORLD)).getWorldServer();
                ((ServerWorldInfo)world.getLevelData()).setDayTime(getDayTime(world));
                world.players().stream().filter(LivingEntity::isSleeping).forEach(p -> p.stopSleepInBed(false, false));
            }, ServerWorld.OVERWORLD);
        } catch (Exception ex) {
            PenguinLib.LOGGER.warn("Error encountered when trying to load the Morpheus Plugin from Simply Seasons.");
        }
    }
}
