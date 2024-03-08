package uk.joshiejack.simplyseasons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import uk.joshiejack.penguinlib.client.gui.HUDRenderer;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.renderer.BlizzardSound;
import uk.joshiejack.simplyseasons.client.renderer.SeasonsHUDRender;
import uk.joshiejack.simplyseasons.plugin.SereneSeasonsPlugin;

import java.util.*;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSClient {
    public static final Map<Season, Music> SEASON_TO_MUSIC = new HashMap<>();
    public static final Lazy<SeasonsHUDRender> HUD = Lazy.of(SeasonsHUDRender::new);

    @Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
    public static class HUDLoader {
        private static final List<ResourceKey<Level>> REGISTERED = new ArrayList<>();

        @SubscribeEvent
        public static void onDatabaseLoad(DatabaseLoadedEvent event) {
            if (SSClientConfig.enableHUD.get()) {
                REGISTERED.forEach(key -> HUDRenderer.RENDERERS.remove(key));
                REGISTERED.clear();
                event.table("seasonal_worlds").rows().forEach(row -> {
                    ResourceLocation rl = row.getRL("world");
                    if (rl != null) {
                        ResourceKey<Level> world = ResourceKey.create(Registries.DIMENSION, rl);
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
            NeoForge.EVENT_BUS.register(new BlizzardSound());
        SEASON_TO_MUSIC.put(Season.SPRING, Musics.createGameMusic(SimplySeasons.SSSounds.SPRING));
        SEASON_TO_MUSIC.put(Season.SUMMER, Musics.createGameMusic(SimplySeasons.SSSounds.SUMMER));
        SEASON_TO_MUSIC.put(Season.AUTUMN, Musics.createGameMusic(SimplySeasons.SSSounds.AUTUMN));
        SEASON_TO_MUSIC.put(Season.WINTER, Musics.createGameMusic(SimplySeasons.SSSounds.WINTER));
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
    public static class WorldUpdater {
        @SubscribeEvent
        public static void onEntityJoinedWorld(EntityJoinLevelEvent event) {
            if (event.getLevel().isClientSide && event.getEntity() instanceof Player) {
                Optional<ISeasonProvider> provider = SSeasonsAPI.instance().getSeasonProvider(event.getLevel().dimension());
                provider.ifPresent((p) -> {
                    p.recalculate(event.getLevel());
                    if (!SereneSeasonsPlugin.loaded)
                        Minecraft.getInstance().levelRenderer.allChanged();

                });
            }
        }
    }
}
