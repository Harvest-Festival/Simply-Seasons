package uk.joshiejack.simplyseasons.world.season;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import uk.joshiejack.penguinlib.events.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.loot.SeasonPredicate;
import uk.joshiejack.simplyseasons.plugins.SereneSeasonsPlugin;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SeasonalCrops {
    private static final Map<Block, SeasonPredicate> REGISTRY = Maps.newHashMap();
    public static final Map<Item, SeasonPredicate> ITEMS = new HashMap<>();

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
    public static void onApplyBonemeal(BonemealEvent event) {
        if (!(event.getWorld() instanceof ServerWorld)) return;
        SeasonPredicate predicate = REGISTRY.get(event.getWorld().getBlockState(event.getPos()).getBlock());
        if (predicate != null && !predicate.matches((ServerWorld) event.getWorld(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        if (SereneSeasonsPlugin.loaded) return; //let them handle this
        REGISTRY.clear(); //Reloading
        event.table("growth_seasons").rows().forEach(row -> {
            ResourceLocation registry = row.getRL("item/block");
            SeasonPredicate predicate = SeasonPredicate.REGISTRY.get(row.get("season predicate").toString());
            if (predicate != null) {
                Item item = ForgeRegistries.ITEMS.getValue(registry);
                if (item != null) {
                    Block block = item instanceof BlockItem ? ((BlockItem) item).getBlock() : ForgeRegistries.BLOCKS.getValue(item.getRegistryName());
                    if (block != null) {
                        REGISTRY.put(block, predicate);
                        ITEMS.put(item, predicate);
                    }
                } else {
                    Block block = ForgeRegistries.BLOCKS.getValue(registry);
                    if (block != null)
                        REGISTRY.put(block, predicate);
                }
            }
        });
    }
}