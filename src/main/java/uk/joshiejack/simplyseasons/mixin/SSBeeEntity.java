package uk.joshiejack.simplyseasons.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.plugin.ResourcefulBeesPlugin;

import java.util.Set;

@Mixin(value = Bee.class, priority = 999)
public abstract class SSBeeEntity extends Animal {
    @Shadow
    Bee.BeePollinateGoal beePollinateGoal;
    @Shadow
    private Bee.BeeGoToKnownFlowerGoal goToKnownFlowerGoal;

    @Shadow
    protected abstract boolean isHiveNearFire();

    @Shadow public abstract void setStayOutOfHiveCountdown(int stayOutOfHiveCountdown);

    @Unique
    private Goal simplySeasons$findPollinationTargetGoal;

    protected SSBeeEntity(EntityType<? extends Animal> type, Level world) {
        super(type, world);
    }

    /**
     * Saves a copy of the pollination and wander goals for this bee
     **/
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Inject(method = "registerGoals", at = @At(value = "TAIL"))
    protected void registerGoals(CallbackInfo ci) {
        if (ResourcefulBeesPlugin.loaded) return;
        if (SimplySeasons.SSConfig.enableBeeInactivityInWinter.get())
            simplySeasons$findPollinationTargetGoal = this.goalSelector.availableGoals.stream().filter(goal -> goal.getGoal() instanceof Bee.BeeGrowCropGoal).findFirst().get().getGoal();
    }

    /**
     * Forces bees to stay in the hives all throughout the winter!
     **/
    @Inject(method = "wantsToEnterHive", at = @At(value = "HEAD"), cancellable = true)
    protected void wantsToEnterHive(CallbackInfoReturnable<Boolean> cir) {
        if (ResourcefulBeesPlugin.loaded) return;
        if (SimplySeasons.SSConfig.enableBeeInactivityInWinter.get()) {
            SSeasonsAPI.instance().getSeasonProvider(level().dimension())
                    .ifPresent(provider -> {
                        Set<Season> seasons = provider.getSeasonsAt(level(), blockPosition());
                        if (seasons.size() == 1 && seasons.contains(Season.WINTER)) { //If we have more than one season at this location the bee is good
                            if (!isHiveNearFire()) {
                                setStayOutOfHiveCountdown(0);
                                cir.setReturnValue(true);
                            }
                        }
                    });
        }
    }

    /**
     * Checks the season every 5 seconds
     * and removes/adds the goals
     **/
    @Inject(method = "tick", at = @At(value = "TAIL"))
    protected void updateGoals(CallbackInfo ci) {
        if (ResourcefulBeesPlugin.loaded) return;
        if (SimplySeasons.SSConfig.enableBeeInactivityInWinter.get() && !level().isClientSide && level().getDayTime() % 1200 == 0
                && beePollinateGoal != null && simplySeasons$findPollinationTargetGoal != null && goToKnownFlowerGoal != null) {
            SSeasonsAPI.instance().getSeasonProvider(level().dimension())
                    .ifPresent(provider -> {
                        Set<Season> seasons = provider.getSeasonsAt(level(), blockPosition());
                        //Remove the goals first to be sure, that they aren't added twice
                        this.goalSelector.removeGoal(beePollinateGoal);
                        this.goalSelector.removeGoal(goToKnownFlowerGoal);
                        this.goalSelector.removeGoal(simplySeasons$findPollinationTargetGoal);
                        if (!(seasons.size() == 1 && seasons.contains(Season.WINTER))) {
                            this.goalSelector.addGoal(4, beePollinateGoal);
                            this.goalSelector.addGoal(6, goToKnownFlowerGoal);
                            this.goalSelector.addGoal(7, simplySeasons$findPollinationTargetGoal);
                        }
                    });
        }
    }
}
