package uk.joshiejack.simplyseasons.world.season;

import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
import uk.joshiejack.simplyseasons.world.SSServerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SeasonalCrops {
    public static final ITag.INamedTag<Block> JUNK = BlockTags.createOptional(new ResourceLocation(SimplySeasons.MODID, "junk"));
    public static final ITag.INamedTag<Block> INDESTRUCTIBLE = BlockTags.createOptional(new ResourceLocation(SimplySeasons.MODID, "indestructible"));
    private static final Map<Block, SeasonPredicate> BLOCKS = Maps.newHashMap();
    public static final Map<Item, SeasonPredicate> ITEMS = new HashMap<>();

    public static boolean isSimplySeasonsGrowthDisabled() {
        return SereneSeasonsPlugin.loaded && !SSServerConfig.useSSCropsHandler.get();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onSeedInteract(PlayerInteractEvent.RightClickBlock event) {
        if (isSimplySeasonsGrowthDisabled()) return;
        SeasonPredicate predicate = ITEMS.get(event.getItemStack().getItem());
        if (SimplySeasons.SSConfig.disableOutofSeasonPlanting.get() &&
                predicate != null && !predicate.matches(event.getWorld(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (isSimplySeasonsGrowthDisabled() || !(event.getWorld() instanceof ServerWorld)) return;
        SeasonPredicate predicate = BLOCKS.get(event.getState().getBlock());
        if (predicate != null && !predicate.matches((ServerWorld) event.getWorld(), event.getPos()) &&
                SimplySeasons.SSConfig.cropOutOfSeasonEffect.get().predicate.test(event.getWorld(), event.getPos())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTreeGrow(SaplingGrowTreeEvent event) {
        if (isSimplySeasonsGrowthDisabled() || !(event.getWorld() instanceof ServerWorld)) return;
        SeasonPredicate predicate = BLOCKS.get(event.getWorld().getBlockState(event.getPos()).getBlock());
        if (predicate != null && !predicate.matches((ServerWorld) event.getWorld(), event.getPos()) &&
                SimplySeasons.SSConfig.cropOutOfSeasonEffect.get().predicate.test(event.getWorld(), event.getPos())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onApplyBonemeal(BonemealEvent event) {
        if (isSimplySeasonsGrowthDisabled() || !(event.getWorld() instanceof ServerWorld)) return;
        SeasonPredicate predicate = BLOCKS.get(event.getWorld().getBlockState(event.getPos()).getBlock());
        if (predicate != null && !predicate.matches(event.getWorld(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        BLOCKS.clear(); //Reloading
        ITEMS.clear(); //Reloading
        event.table("growth_seasons").rows().forEach(row -> {
            ResourceLocation registry = row.getRL("item/block");
            SeasonPredicate predicate = SeasonPredicate.REGISTRY.get(row.get("season predicate").toString());
            if (predicate != null) {
                Item item = ForgeRegistries.ITEMS.getValue(registry);
                if (item != Items.AIR) {
                    Block block = item instanceof BlockItem ? ((BlockItem) item).getBlock() : ForgeRegistries.BLOCKS.getValue(item.getRegistryName());
                    if (block != Blocks.AIR) {
                        BLOCKS.put(block, predicate);
                        ITEMS.put(item, predicate);
                    }
                } else {
                    Block block = ForgeRegistries.BLOCKS.getValue(registry);
                    if (block != Blocks.AIR)
                        BLOCKS.put(block, predicate);
                }
            }
        });
    }

    public enum CropOutOfSeasonEffect {
        SLOW_GROWTH((w, p) -> w.getRandom().nextFloat() >= 0.01F),
        NO_GROWTH((w, p) -> true),
        REPLACE_WITH_JUNK((w, p) -> {
            if (canDestroy(w.getBlockState(p)))
                return w.setBlock(p, JUNK.getRandomElement(w.getRandom()).defaultBlockState(), 3);
            return true;
        }),
        SET_TO_AIR((w, p) -> {
            if (canDestroy(w.getBlockState(p)))
                return w.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            return true;
        });

        public final BiPredicate<IWorld, BlockPos> predicate;

        CropOutOfSeasonEffect(BiPredicate<IWorld, BlockPos> predicate) {
            this.predicate = predicate;
        }

        private static boolean canDestroy(BlockState state) {
            return !(state.getBlock() instanceof LeavesBlock)
                    && !(state.getBlock() instanceof SaplingBlock)
                    && !INDESTRUCTIBLE.contains(state.getBlock());
        }
    }
}