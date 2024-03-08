package uk.joshiejack.simplyseasons.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.plugin.ResourcefulBeesPlugin;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Mixin(value = BeehiveBlockEntity.class, priority = 999)
public abstract class SSBeehiveBlockEntity extends BlockEntity {
    public SSBeehiveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Forces bees to stay in the hives all throughout the winter!
     **/
    @Inject(method = "releaseOccupant", at = @At(value = "HEAD"), cancellable = true)
    private static void releaseOccupant(Level level, BlockPos pos, BlockState state, BeehiveBlockEntity.BeeData data, @Nullable List<Entity> list,
                                          BeehiveBlockEntity.BeeReleaseStatus status, @Nullable BlockPos p_155143_, CallbackInfoReturnable<Boolean> cir) {
        if (ResourcefulBeesPlugin.loaded) return;
        if (SimplySeasons.SSConfig.enableBeeInactivityInWinter.get()) {
            assert level != null;
            SSeasonsAPI.instance().getSeasonProvider(level.dimension())
                    .ifPresent(provider -> {
                        Set<Season> seasons = provider.getSeasonsAt(level, pos);
                        if (seasons.size() == 1 && seasons.contains(Season.WINTER)) {
                            if (status != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
                                cir.setReturnValue(false);
                            }
                        }
                    });
        }
    }
}
