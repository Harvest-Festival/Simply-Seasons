package uk.joshiejack.simplyseasons.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClient;
import uk.joshiejack.simplyseasons.client.SSClientConfig;

import java.util.Optional;

@SuppressWarnings("ConstantConditions")
@Mixin(value = MusicManager.class, priority = 999)
public class SSMusicTicker {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSituationalMusic()Lnet/minecraft/sounds/Music;"))
    public Music addSeasonalMusic(Minecraft minecraft) {
        Music music = minecraft.getSituationalMusic();
        if (music == Musics.GAME && SSClientConfig.seasonalMusic.get()) {
            Optional<ISeasonProvider> provider = SSeasonsAPI.instance().getSeasonProvider(minecraft.level.dimension());
            if (provider.isPresent()) {
                Season season = provider.get().getSeason(minecraft.level);
                return SSClient.SEASON_TO_MUSIC.get(season);
            }
        }

        return music;
    }
}