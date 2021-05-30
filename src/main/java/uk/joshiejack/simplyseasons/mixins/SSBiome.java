package uk.joshiejack.simplyseasons.mixins;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

@Mixin(Biome.class)
public abstract class SSBiome {
    @Redirect(method = "shouldSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    public float getTemperatureShouldSnow(Biome biome, BlockPos pos) {
        return 0F; //Ignore this callback
    }

    @Inject(method = "shouldSnow", at = @At("HEAD"), cancellable = true)
    public void shouldSnow(IWorldReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world instanceof World) {
            float temperature = SeasonalWorlds.getTemperature((World) world, world.getBiome(pos), pos);
            if (temperature >= 0.15F)
                cir.setReturnValue(false);
        }
    }

    @Redirect(method = "shouldFreeze(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    public float getTemperatureShouldFreeze(Biome biome, BlockPos pos) {
        return 0F; //Ignore this callback
    }

    @Inject(method = "shouldFreeze(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Z)Z", at = @At("HEAD"), cancellable = true)
    public void shouldFreeze(IWorldReader world, BlockPos pos, boolean bool, CallbackInfoReturnable<Boolean> cir) {
        if (world instanceof World) {
            float temperature = SeasonalWorlds.getTemperature((World) world, world.getBiome(pos), pos);
            if (temperature >= 0.15F)
                cir.setReturnValue(false);
        }
    }
}
