package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.client.SSConfig;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;
import uk.joshiejack.simplyseasons.api.Weather;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
public class FogRenderer {
    private static int fogValue = 0;
    private static int fogTarget = 0;

    @SubscribeEvent
    public static void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (BetterWeatherPlugin.loaded || SSConfig.fogDensity.get() <= 0) return;
        Minecraft mc = Minecraft.getInstance();
        mc.level.getCapability(SSeasonsAPI.WEATHER_CAPABILITY).ifPresent(provider -> {
            if (!event.getInfo().getBlockAtCamera().getMaterial().isLiquid()) {
                Weather weather = provider.getWeather(mc.level);
                boolean isSnow = (weather == Weather.RAIN || weather == Weather.STORM) &&
                        SeasonalWorlds.getTemperature(mc.level, mc.level.getBiome(mc.player.blockPosition()), mc.player.blockPosition()) < 0.15F;
                if (fogTarget != fogValue) {
                    if (fogTarget > fogValue)
                        fogValue++;
                    else
                        fogValue--;
                }

                if (isSnow || weather == Weather.FOG) {
                    switch (weather) {
                        case STORM:
                            fogTarget = SSConfig.fogDensity.get() * 4;
                            break;
                        case RAIN:
                            fogTarget = SSConfig.fogDensity.get();
                            break;
                        default:
                            fogTarget = SSConfig.fogDensity.get() * 2;
                            break;
                    }
                } else fogTarget = 0;

                if (fogValue != 0) {
                    event.setDensity(fogValue/1000F);
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
                boolean isSnow = (weather == Weather.RAIN || weather == Weather.STORM) &&
                        SeasonalWorlds.getTemperature(mc.level, mc.level.getBiome(mc.player.blockPosition()), mc.player.blockPosition()) < 0.15F;
                if (isSnow) {
                    event.setRed(1F);
                    event.setBlue(1F);
                    event.setGreen(1F);
                } else if (weather == Weather.FOG) {
                    event.setRed(0.55F);
                    event.setBlue(0.55F);
                    event.setGreen(0.55F);
                }
            }
        });
    }
}
