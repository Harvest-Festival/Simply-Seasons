package uk.joshiejack.simplyseasons.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

@Mixin(value = SnowGolemEntity.class, priority = 999)
public class SSSnowGolemEntity extends GolemEntity {
    protected SSSnowGolemEntity(EntityType<? extends GolemEntity> type, World world) {
        super(type, world);
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    public float getTemperatureAIStep(Biome biome, BlockPos pos) {
        return SeasonalWorlds.getTemperature(this.level, biome, pos);
    }
}
