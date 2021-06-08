package uk.joshiejack.simplyseasons.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.client.audio.MusicTicker;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClient;
import uk.joshiejack.simplyseasons.client.SSClientConfig;

@Mixin(MusicTicker.class)
public class SSMusicTicker {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSituationalMusic()Lnet/minecraft/client/audio/BackgroundMusicSelector;"))
    public BackgroundMusicSelector addSeasonalMusic(Minecraft minecraft) {
        BackgroundMusicSelector music = minecraft.getSituationalMusic();
        if (music == BackgroundMusicTracks.GAME && SSClientConfig.seasonalMusic.get()) {
            LazyOptional<ISeasonProvider> provider = minecraft.level.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
            if (provider.isPresent()) {
                Season season = provider.resolve().get().getSeason(minecraft.level);
                return SSClient.SEASON_TO_MUSIC.get(season);
            }
        }

        return music;
    }
}
