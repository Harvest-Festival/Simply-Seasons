package uk.joshiejack.simplyseasons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import uk.joshiejack.penguinlib.client.gui.HUDRenderer;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.client.renderer.SeasonsHUDRender;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSClient {
    @SubscribeEvent
    public static void onClientLoad(FMLClientSetupEvent event) {
        if (SSConfig.enableHUD.get())
            HUDRenderer.RENDERERS.put(World.OVERWORLD, new SeasonsHUDRender());
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
    public static class WorldUpdater {
        @SubscribeEvent
        public static void onEntityJoinedWorld(EntityJoinWorldEvent event) {
            if (event.getWorld().isClientSide && event.getEntity() instanceof PlayerEntity)
                event.getWorld().getCapability(SSeasonsAPI.SEASONS_CAPABILITY)
                        .ifPresent(provider -> {
                            provider.recalculate(event.getWorld());
                            Minecraft.getInstance().levelRenderer.allChanged();
                        });
        }
    }
}
