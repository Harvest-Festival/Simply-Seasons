package uk.joshiejack.simplyseasons.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@Mixin(BeehiveTileEntity.class)
public abstract class SSBeeHiveTileEntity extends TileEntity {
    public SSBeeHiveTileEntity(TileEntityType<?> type) {
        super(type);
    }

    /**
     * Forces bees to stay in the hives all throughout the winter!
     **/
    @Inject(method = "releaseOccupant", at = @At(value = "HEAD"), cancellable = true)
    protected void releaseOccupant(BlockState blockState, BeehiveTileEntity.Bee bee, @Nullable List<Entity> entity,
                                   BeehiveTileEntity.State state, CallbackInfoReturnable<Boolean> cir) {
        assert level != null;
        level.getCapability(SSeasonsAPI.SEASONS_CAPABILITY)
                .ifPresent(provider -> {
                    Set<Season> seasons = provider.getSeasonsAt(level, worldPosition);
                    if (seasons.size() == 1 && seasons.contains(Season.WINTER)) {
                        if (state != BeehiveTileEntity.State.EMERGENCY) {
                            cir.setReturnValue(false);
                        }
                    }
                });
    }
}
