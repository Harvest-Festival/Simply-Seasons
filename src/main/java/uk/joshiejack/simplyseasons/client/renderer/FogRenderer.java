package uk.joshiejack.simplyseasons.client.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Weather;
import uk.joshiejack.simplyseasons.client.SSClientConfig;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
public class FogRenderer {
    private static final Cache<Biome, Boolean> IS_DRY = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    private static int fogValue = 0;
    private static int fogTarget = 0;

    private static boolean isDry(World world, Biome biome) {
        try {
            return IS_DRY.get(biome, () -> {
                RegistryKey<Biome> key = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(biome).get();
                return BiomeDictionary.hasType(key, BiomeDictionary.Type.SAVANNA) ||
                        BiomeDictionary.hasType(key, BiomeDictionary.Type.SANDY);
            });
        } catch (ExecutionException ex) {
            return false;
        }
    }

    @SubscribeEvent
    public static void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (BetterWeatherPlugin.loaded || SSClientConfig.overallFogDensity.get() <= 0) return;
        Minecraft mc = Minecraft.getInstance();
        mc.level.getCapability(SSeasonsAPI.WEATHER_CAPABILITY).ifPresent(provider -> {
            if (!event.getInfo().getBlockAtCamera().getMaterial().isLiquid()) {
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

                Biome biome = mc.level.getBiome(playerHead);
                boolean isDry = isDry(mc.level, biome);
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
                    event.setDensity(fogValue / 10000F);
                    event.setCanceled(true);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onFogColor(EntityViewRenderEvent.FogColors event) {
        if (BetterWeatherPlugin.loaded) return;
        Minecraft mc = Minecraft.getInstance();
        mc.level.getCapability(SSeasonsAPI.WEATHER_CAPABILITY).ifPresent(provider -> {
            if (!event.getInfo().getBlockAtCamera().getMaterial().isLiquid()) {
                Weather weather = provider.getWeather(mc.level);
                BlockPos playerHead = mc.player.blockPosition().above();
                boolean isSnow = (weather == Weather.RAIN || weather == Weather.STORM) &&
                        SeasonalWorlds.getTemperature(mc.level, mc.level.getBiome(playerHead), playerHead) < 0.15F;
                if (isSnow) {
                    event.setRed(1F);
                    event.setGreen(1F);
                    event.setBlue(1F);
                } else if (weather == Weather.FOG) {
                    Biome biome = mc.level.getBiome(playerHead);
                    boolean isDry = isDry(mc.level, biome);
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
