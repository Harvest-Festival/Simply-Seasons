package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.TickEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Weather;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

@OnlyIn(Dist.CLIENT)
public class BlizzardSound {
    private boolean playing = false;

    @SubscribeEvent
    public void onWorldTick(TickEvent.PlayerTickEvent event) {
        Level world = event.player.level();
        if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END && (world.getDayTime() % 300 == 1 || (!playing && world.isThundering()))) {
            SSeasonsAPI.instance().getWeatherProvider(world.dimension()).ifPresent(provider -> {
                Weather weather = provider.getWeather(world);
                Player player = event.player;
                if (weather == Weather.STORM && world.canSeeSky(player.blockPosition().above()) &&
                        SeasonalWorlds.getTemperature(world, world.getBiome(player.blockPosition()), player.blockPosition()) < 0.15F) {
                    world.playLocalSound(player.blockPosition(), SimplySeasons.SSSounds.BLIZZARD.value(), SoundSource.WEATHER, 0.5F, 1F, false);
                    playing = true;
                } else playing = false;
            });
        }
    }
}
