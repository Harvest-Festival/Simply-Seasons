package uk.joshiejack.simplyseasons.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.DataMapProvider;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.loot.SeasonPredicate;
import uk.joshiejack.simplyseasons.world.season.SeasonalCrops;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class SSDataMapProvider extends DataMapProvider {
    public SSDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        final var cropData = builder(SeasonalCrops.GROWTH_SEASONS);
        assignSeasons(cropData, Items.CHERRY_SAPLING, Season.SUMMER);
        assignSeasons(cropData, Items.MANGROVE_PROPAGULE, Season.SPRING, Season.SUMMER);
        assignSeasons(cropData, Items.TORCHFLOWER_SEEDS, Season.SUMMER);
        assignSeasons(cropData, Items.PITCHER_PLANT, Season.SPRING, Season.SUMMER);
    }

    @SuppressWarnings("deprecation")
    private void assignSeasons(Builder<SeasonPredicate, Item> tickers, ItemLike item, Season... seasons) {
        tickers.add(item.asItem().builtInRegistryHolder(), new SeasonPredicate(Arrays.asList(seasons)), false);
    }
}