package uk.joshiejack.simplyseasons.world.season;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.SaplingGrowTreeEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.plugin.SereneSeasonsPlugin;
import uk.joshiejack.simplyseasons.world.SSServerConfig;
import uk.joshiejack.simplyseasons.world.loot.SeasonPredicate;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SeasonalCrops {
    public static final DataMapType<Item, SeasonPredicate> GROWTH_SEASONS = DataMapType.builder(new ResourceLocation(SimplySeasons.MODID, "growth_seasons"), Registries.ITEM,
            SeasonPredicate.CODEC).synced(SeasonPredicate.CODEC, true).build();
    public static final TagKey<Block> JUNK = BlockTags.create(new ResourceLocation(SimplySeasons.MODID, "junk"));
    public static final TagKey<Block> INDESTRUCTIBLE = BlockTags.create(new ResourceLocation(SimplySeasons.MODID, "indestructible"));
    private static final Map<Block, SeasonPredicate> BLOCKS = Maps.newHashMap();
    private static final Map<Item, SeasonPredicate> ITEMS = new HashMap<>();

    @Mod.EventBusSubscriber(modid = SimplySeasons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Register {
        @SubscribeEvent
        public static void registerDataMaps(RegisterDataMapTypesEvent event) {
            event.register(GROWTH_SEASONS);
        }
    }

    @SuppressWarnings("deprecation")
    public static @Nullable SeasonPredicate blockPredicate(BlockState block) {
        SeasonPredicate predicate = block.getBlock().asItem().builtInRegistryHolder().getData(GROWTH_SEASONS);
        return predicate != null ? predicate : BLOCKS.get(block.getBlock());
    }

    @SuppressWarnings("deprecation")
    public static @Nullable SeasonPredicate itemPredicate(Item item) {
        SeasonPredicate predicate = item.builtInRegistryHolder().getData(GROWTH_SEASONS);
        return predicate != null ? predicate : ITEMS.get(item);
    }

    public static boolean isSimplySeasonsGrowthDisabled() {
        return SereneSeasonsPlugin.loaded && !SSServerConfig.useSSCropsHandler.get();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onSeedInteract(PlayerInteractEvent.RightClickBlock event) {
        if (isSimplySeasonsGrowthDisabled()) return;
        SeasonPredicate predicate = itemPredicate(event.getItemStack().getItem());
        if (SimplySeasons.SSConfig.disableOutofSeasonPlanting.get() &&
                predicate != null && !predicate.matches(event.getLevel(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (isSimplySeasonsGrowthDisabled() || !(event.getLevel() instanceof ServerLevel)) return;
        SeasonPredicate predicate = blockPredicate(event.getState());
        if (predicate != null && !predicate.matches((ServerLevel) event.getLevel(), event.getPos()) &&
                SimplySeasons.SSConfig.cropOutOfSeasonEffect.get().predicate.test(event.getLevel(), event.getPos())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTreeGrow(SaplingGrowTreeEvent event) {
        if (isSimplySeasonsGrowthDisabled() || !(event.getLevel() instanceof ServerLevel)) return;
        SeasonPredicate predicate = blockPredicate(event.getLevel().getBlockState(event.getPos()));
        if (predicate != null && !predicate.matches((ServerLevel) event.getLevel(), event.getPos()) &&
                SimplySeasons.SSConfig.cropOutOfSeasonEffect.get().predicate.test(event.getLevel(), event.getPos())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onApplyBoneMeal(BonemealEvent event) {
        if (isSimplySeasonsGrowthDisabled() || !(event.getLevel() instanceof ServerLevel)) return;
        SeasonPredicate predicate = blockPredicate(event.getLevel().getBlockState(event.getPos()));
        if (predicate != null && !predicate.matches(event.getLevel(), event.getPos())) {
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
                Item item = BuiltInRegistries.ITEM.get(registry);
                if (item != Items.AIR) {
                    Block block = item instanceof BlockItem ? ((BlockItem) item).getBlock() : BuiltInRegistries.BLOCK.get(BuiltInRegistries.ITEM.getKey(item));
                    if (block != Blocks.AIR) {
                        BLOCKS.put(block, predicate);
                        ITEMS.put(item, predicate);
                    }
                } else {
                    Block block = BuiltInRegistries.BLOCK.get(registry);
                    if (block != Blocks.AIR)
                        BLOCKS.put(block, predicate);
                }
            }
        });
    }

    public enum CropOutOfSeasonEffect {
        NONE((w, p) -> false),
        SLOW_GROWTH((w, p) -> w.getRandom().nextFloat() >= 0.01F),
        NO_GROWTH((w, p) -> true),
        REPLACE_WITH_JUNK((w, p) -> {
            if (canDestroy(w.getBlockState(p))) {
                Optional<Holder<Block>> holder = BuiltInRegistries.BLOCK.getTag(JUNK).flatMap(tag -> tag.getRandomElement(w.getRandom()));
                if (holder.isPresent())
                    return w.setBlock(p, holder.get().value().defaultBlockState(), 3);
            }

            return true;
        }),
        SET_TO_AIR((w, p) -> {
            if (canDestroy(w.getBlockState(p)))
                return w.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            return true;
        });

        public final BiPredicate<LevelAccessor, BlockPos> predicate;

        CropOutOfSeasonEffect(BiPredicate<LevelAccessor, BlockPos> predicate) {
            this.predicate = predicate;
        }

        private static boolean canDestroy(BlockState state) {
            return !(state.getBlock() instanceof LeavesBlock)
                    && !(state.getBlock() instanceof SaplingBlock)
                    && !state.is(INDESTRUCTIBLE);
        }
    }
}