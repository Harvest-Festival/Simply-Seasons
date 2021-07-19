package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Weather;
import uk.joshiejack.simplyseasons.plugins.BetterWeatherPlugin;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

@OnlyIn(Dist.CLIENT)
public class BlizzardSound {
    private boolean playing = false;

    @SubscribeEvent
    public void onWorldTick(TickEvent.PlayerTickEvent event) {
        if (BetterWeatherPlugin.loaded) return;
        World world = event.player.level;;
        if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END && (world.getDayTime() % 300 == 1 || (!playing && world.isThundering()))) {
            world.getCapability(SSeasonsAPI.WEATHER_CAPABILITY).ifPresent(provider -> {
                Weather weather = provider.getWeather(world);
                PlayerEntity player = event.player;
                if (weather == Weather.STORM && world.canSeeSky(player.blockPosition().above()) &&
                        SeasonalWorlds.getTemperature(world, world.getBiome(player.blockPosition()), player.blockPosition()) < 0.15F) {
                    ((ClientWorld)world).playLocalSound(player.blockPosition(), SimplySeasons.SSSounds.BLIZZARD.get(), SoundCategory.WEATHER, 0.5F, 1F, false);
                    playing = true;
                } else playing = false;
            });
        }
    }
}
