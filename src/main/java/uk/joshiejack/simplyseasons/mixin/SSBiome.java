package uk.joshiejack.simplyseasons.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

@Mixin(value = Biome.class, priority = 999)
public abstract class SSBiome {
    @Redirect(method = "shouldSnow", at = @At(value = "INVOKE", target = "net/minecraft/world/level/biome/Biome.warmEnoughToRain (Lnet/minecraft/core/BlockPos;)Z"))
    public boolean getTemperatureShouldSnow(Biome biome, BlockPos pos) {
        return false; //Ignore this callback
    }

    @Inject(method = "shouldSnow", at = @At("HEAD"), cancellable = true)
    public void shouldSnow(LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (SeasonalWorlds.warmEnoughToRain(world, simplySeasons$asBiome(), pos))
            cir.setReturnValue(false);
    }

    @Redirect(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At(value = "INVOKE", target = "net/minecraft/world/level/biome/Biome.warmEnoughToRain (Lnet/minecraft/core/BlockPos;)Z"))
    public boolean warmEnoughToRain(Biome biome, BlockPos pos) {
        return false; //Ignore this callback
    }

    @Inject(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At("HEAD"), cancellable = true)
    public void shouldFreeze(LevelReader world, BlockPos pos, boolean bool, CallbackInfoReturnable<Boolean> cir) {
        if (SeasonalWorlds.warmEnoughToRain(world, simplySeasons$asBiome(), pos))
            cir.setReturnValue(false);
    }

    @Unique
    private Biome simplySeasons$asBiome() {
        return (Biome) (Object) this;
    }
}