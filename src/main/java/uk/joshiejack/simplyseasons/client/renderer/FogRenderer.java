package uk.joshiejack.simplyseasons.client.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.Tags;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Weather;
import uk.joshiejack.simplyseasons.client.SSClientConfig;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
public class FogRenderer {
    private static final Cache<Holder<Biome>, Boolean> IS_DRY = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    private static int fogValue = 0;
    private static int fogTarget = 0;

    private static boolean isDry(Holder<Biome> biome) {
        try {
            return IS_DRY.get(biome, () -> biome.is(BiomeTags.IS_SAVANNA) || biome.is(Tags.Biomes.IS_SANDY));
        } catch (ExecutionException ex) {
            return false;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onFogDensity(ViewportEvent.RenderFog event) {
        if (SSClientConfig.overallFogDensity.get() <= 0) return;
        Minecraft mc = Minecraft.getInstance();
        SSeasonsAPI.instance().getWeatherProvider(mc.level.dimension()).ifPresent(provider -> {
            if (mc.gameRenderer.getMainCamera().getBlockAtCamera().getFluidState() == Fluids.EMPTY.defaultFluidState()) {
                Weather weather = provider.getWeather(mc.level);
                BlockPos playerHead = mc.player.blockPosition().above();
                boolean isSnow = (weather == Weather.RAIN || weather == Weather.STORM) && mc.level.canSeeSky(playerHead) &&
                        SeasonalWorlds.getTemperature(mc.level, mc.level.getBiome(playerHead), playerHead) < 0.15F;
                if (fogTarget != fogValue) {
                    if (fogTarget > fogValue)
                        fogValue++;
                    else
                        fogValue--;
                }

                boolean isDry = isDry(mc.level.getBiome(playerHead));
                if ((isSnow || weather == Weather.FOG) && (!isDry || SSClientConfig.dryFogSetting.get() != SSClientConfig.DryFog.OFF)) {
                    switch (weather) {
                        case STORM:
                            fogTarget = SSClientConfig.overallFogDensity.get() * SSClientConfig.blizzardDensityMultiplier.get();
                            break;
                        case RAIN:
                            fogTarget = SSClientConfig.overallFogDensity.get() * SSClientConfig.snowDensityMultiplier.get();
                            break;
                        default:
                            fogTarget = SSClientConfig.overallFogDensity.get() *
                                    (isDry ? SSClientConfig.dryFogDensityMultiplier.get() : SSClientConfig.fogDensityMultiplier.get());
                            break;
                    }
                } else fogTarget = 0;

                if (fogValue != 0) {
                    event.setFarPlaneDistance(fogValue / 10000F); //TODO: TEST the numbers
                    event.setCanceled(true);
                }
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();
        SSeasonsAPI.instance().getWeatherProvider(mc.level.dimension()).ifPresent(provider -> {
            if (mc.gameRenderer.getMainCamera().getBlockAtCamera().getFluidState() == Fluids.EMPTY.defaultFluidState()) {
                Weather weather = provider.getWeather(mc.level);
                BlockPos playerHead = mc.player.blockPosition().above();
                boolean isSnow = (weather == Weather.RAIN || weather == Weather.STORM) &&
                        SeasonalWorlds.getTemperature(mc.level, mc.level.getBiome(playerHead), playerHead) < 0.15F;
                if (isSnow) {
                    event.setRed(1F);
                    event.setGreen(1F);
                    event.setBlue(1F);
                } else if (weather == Weather.FOG) {
                    boolean isDry = isDry(mc.level.getBiome(playerHead));
                    if (isDry && SSClientConfig.dryFogSetting.get() == SSClientConfig.DryFog.SANDY) {
                        event.setRed(230F / 255F);
                        event.setGreen(218F / 255F);
                        event.setBlue(175F / 255F);
                    } else {
                        event.setRed(0.55F);
                        event.setGreen(0.55F);
                        event.setBlue(0.55F);
                    }
                }
            }
        });
    }
}
