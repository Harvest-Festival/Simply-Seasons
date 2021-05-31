package uk.joshiejack.simplyseasons.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.client.SSClient;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;
import uk.joshiejack.simplyseasons.world.weather.Weather;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SimplySeasons.MODID, value = Dist.CLIENT)
public class FogRenderer {
    private static final BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();
    private static int fogStart = 0;
    private static int fogTarget = 0;

    @SubscribeEvent
    public static void onFogRender(EntityViewRenderEvent.RenderFogEvent event) {
        if (BetterWeatherPlugin.loaded) return;
        Minecraft mc = Minecraft.getInstance();
        mc.level.getCapability(SSeasonsAPI.WEATHER_CAPABILITY).ifPresent(provider -> {
            if (!event.getInfo().getBlockAtCamera().getMaterial().isLiquid()) {
                //Update the fog smoothly
                if (fogTarget != fogStart) {
                    if (fogTarget > fogStart) {
                        fogStart += 5;
                    } else if (fogTarget < fogStart) {
                        fogStart -= 5;
                    }
                }

                blockpos$mutableblockpos.set(mc.player.xo, mc.player.yo, mc.player.zo);
                int i1 = mc.options.graphicsMode.ordinal() > 0 ? 10 : 5;
                int j = MathHelper.floor(mc.player.yo);
                int j2 = mc.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, blockpos$mutableblockpos).getY();
                int k2 = j - i1;
                int l2 = j + i1;

                if (k2 < j2) {
                    k2 = j2;
                }

                if (l2 < j2) {
                    l2 = j2;
                }


                Weather weather = SSClient.INSTANCE.getWeather();
                boolean isSnow = (weather == Weather.RAIN || weather == Weather.STORM) &&
                        SeasonalWorlds.getTemperature(mc.level, mc.level.getBiome(event.getInfo().getBlockPosition()), event.getInfo().getBlockPosition()) < 0.15F;                if (k2 != l2) {
                    if (weather == Weather.FOGGY || isSnow) {
                        switch (weather) {
                            case STORM:
                                fogTarget = -5000;
                                break;
                            case RAIN:
                                fogTarget = -100;
                                break;
                            default:
                                fogTarget = 50;
                                break;
                        }
                    } else fogTarget = 5000;
                } else fogTarget = 100;
                if (blockpos$mutableblockpos.getY() < j2) fogTarget = 5000;

                //If we're snow or resetting the target
                if (isSnow || weather == Weather.FOGGY) {
                    RenderSystem.fogEnd(Math.min(event.getFarPlaneDistance(), 150F) * 0.5F);
                    RenderSystem.fogStart((float) fogStart / 100F);
                }
            } else {
                fogStart = 100;
                fogTarget = 100;
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
                blockpos$mutableblockpos.set(mc.player.xo, mc.player.yo, mc.player.zo);
                boolean isSnow = (weather == Weather.RAIN || weather == Weather.STORM) &&
                        SeasonalWorlds.getTemperature(mc.level, mc.level.getBiome(event.getInfo().getBlockPosition()), event.getInfo().getBlockPosition()) < 0.15F;
                if (isSnow) {
                    event.setRed(1F);
                    event.setBlue(1F);
                    event.setGreen(1F);
                } else if (weather == Weather.FOGGY) {
                    event.setRed(0.55F);
                    event.setBlue(0.55F);
                    event.setGreen(0.55F);
                }
            }
        });
    }
}
