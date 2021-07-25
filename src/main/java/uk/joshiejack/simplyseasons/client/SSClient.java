package uk.joshiejack.simplyseasons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import uk.joshiejack.penguinlib.client.gui.HUDRenderer;
import uk.joshiejack.penguinlib.events.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.renderer.BlizzardSound;
import uk.joshiejack.simplyseasons.client.renderer.SeasonsHUDRender;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;
import uk.joshiejack.simplyseasons.plugins.SereneSeasonsPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSClient {
    public static final Map<Season, BackgroundMusicSelector> SEASON_TO_MUSIC = new HashMap<>();
    public static final Lazy<SeasonsHUDRender> HUD = Lazy.of(SeasonsHUDRender::new);

    @Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
    public static class HUDLoader {
        private static final List<RegistryKey<World>> REGISTERED = new ArrayList<>();

        @SubscribeEvent
        public static void onDatabaseLoad(DatabaseLoadedEvent event) {
            if (SSClientConfig.enableHUD.get()) {
                REGISTERED.forEach(key -> HUDRenderer.RENDERERS.remove(key));
                REGISTERED.clear();
                event.table("seasonal_worlds").rows().forEach(row -> {
                    ResourceLocation rl = row.getRL("world");
                    if (rl != null) {
                        RegistryKey<World> world = RegistryKey.create(Registry.DIMENSION_REGISTRY, rl);
                        REGISTERED.add(world);
                        HUDRenderer.RENDERERS.put(world, HUD.get());
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onClientLoad(FMLClientSetupEvent event) {
        if (SSClientConfig.enableBlizzardNoise.get())
            MinecraftForge.EVENT_BUS.register(new BlizzardSound());
        SEASON_TO_MUSIC.put(Season.SPRING, BackgroundMusicTracks.createGameMusic(SimplySeasons.SSSounds.SPRING.get()));
        SEASON_TO_MUSIC.put(Season.SUMMER, BackgroundMusicTracks.createGameMusic(SimplySeasons.SSSounds.SUMMER.get()));
        SEASON_TO_MUSIC.put(Season.AUTUMN, BackgroundMusicTracks.createGameMusic(SimplySeasons.SSSounds.AUTUMN.get()));
        SEASON_TO_MUSIC.put(Season.WINTER, BackgroundMusicTracks.createGameMusic(SimplySeasons.SSSounds.WINTER.get()));
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
                            if (!SereneSeasonsPlugin.loaded && !BetterWeatherPlugin.loaded)
                                Minecraft.getInstance().levelRenderer.allChanged();
                        });
        }
    }

}
