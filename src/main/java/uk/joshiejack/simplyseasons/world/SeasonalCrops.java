package uk.joshiejack.simplyseasons.world;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.events.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.loot.SeasonPredicate;

import java.util.Map;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SeasonalCrops {
    private static final Map<Block, SeasonPredicate> REGISTRY = Maps.newHashMap();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (!(event.getWorld() instanceof ServerWorld)) return;
        SeasonPredicate predicate = REGISTRY.get(event.getState().getBlock());
        if (predicate != null && !predicate.matches((ServerWorld) event.getWorld(), event.getPos())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTreeGrow(SaplingGrowTreeEvent event) {
        if (!(event.getWorld() instanceof ServerWorld)) return;
        SeasonPredicate predicate = REGISTRY.get(event.getWorld().getBlockState(event.getPos()).getBlock());
        if (predicate != null && !predicate.matches((ServerWorld) event.getWorld(), event.getPos())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        REGISTRY.clear(); //Reloading
        event.table("growth_seasons").rows().forEach(row -> SeasonalCrops.REGISTRY.put(row.block(),
                SeasonPredicate.REGISTRY.get(row.get("season predicate").toString())));
    }
}