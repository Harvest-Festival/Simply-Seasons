package uk.joshiejack.simplyseasons.data;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import uk.joshiejack.penguinlib.data.database.CSVUtils;
import uk.joshiejack.penguinlib.data.generators.AbstractDatabaseProvider;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.api.Weather;

import java.util.Locale;
import java.util.Objects;

public class SSDatabase extends AbstractDatabaseProvider {
    public SSDatabase(DataGenerator gen) {
        super(gen, SimplySeasons.MODID);
    }

    @Override
    protected void addDatabaseEntries() {
        addTimeUnit("season_length_multiplier", 4);
        addTimeUnit("dedicated_server_season_multiplier", 3);
        addSeasonData(Season.SPRING, TextFormatting.GREEN, 0, "0x80B76C", "0", "0x87CEFA", 6000, 20500);
        addSeasonData(Season.SUMMER, TextFormatting.YELLOW, 0.1, "0", "0x4A9C2E", "0x79a7ff", 5000, 21500);
        addSeasonData(Season.AUTUMN, TextFormatting.GOLD, -0.1, "0xB25900", "0xFF9900", "0x8CBED6", 7000, 19000);
        addSeasonData(Season.WINTER, TextFormatting.GRAY, -0.7, "0xFFFFFF", "0xE6E6E6", "0xBFFFFF", 8000, 16500);
        addSeasonData(Season.WET, TextFormatting.BLUE, -0.1, "0", "0x55FF1C", "0x334873", 0, 0);
        addSeasonData(Season.DRY, TextFormatting.GOLD, 0, "0", "0", "0x638DBF", 0, 0);
        addSeasonToWorld(World.OVERWORLD, Season.MAIN);
        addSeasonPredicate("spring", Season.SPRING);
        addSeasonPredicate("summer", Season.SUMMER);
        addSeasonPredicate("autumn", Season.AUTUMN);
        addSeasonPredicate("winter", Season.WINTER);
        addSeasonPredicate("late", Season.SUMMER);
        addSeasonPredicate("late", Season.AUTUMN);
        addSeasonPredicate("desert", Season.DRY);
        addSeasonPredicate("desert", Season.SUMMER);
        addSeasonPredicate("jungle", Season.WET);
        addSeasonPredicate("jungle", Season.SPRING);
        addSeasonPredicate("jungle", Season.SUMMER);
        addSeasonPredicate("not_winter", Season.SPRING);
        addSeasonPredicate("not_winter", Season.SUMMER);
        addSeasonPredicate("not_winter", Season.AUTUMN);
        addSeasonPredicate("all_year", Season.SPRING);
        addSeasonPredicate("all_year", Season.SUMMER);
        addSeasonPredicate("all_year", Season.AUTUMN);
        addSeasonPredicate("all_year", Season.WINTER);
        addCropGrowth("not_winter", Items.WHEAT_SEEDS);
        addCropGrowth("autumn", Items.CARROT);
        addCropGrowth("spring", Items.POTATO);
        addCropGrowth("autumn", Items.BEETROOT_SEEDS);
        addCropGrowth("late", Items.MELON_SEEDS);
        addCropGrowth("late", Items.PUMPKIN_SEEDS);
        addCropGrowth("jungle", Items.COCOA_BEANS);
        addCropGrowth("jungle", Items.SUGAR_CANE);
        addCropGrowth("jungle", Items.BAMBOO);
        addCropGrowth("desert", Items.CACTUS);
        addCropGrowth("late", Items.SWEET_BERRIES);
        addCropGrowth("not_winter", Items.OAK_SAPLING);
        addCropGrowth("not_winter", Items.SPRUCE_SAPLING);
        addCropGrowth("not_winter", Items.BIRCH_SAPLING);
        addCropGrowth("jungle", Items.JUNGLE_SAPLING);
        addCropGrowth("not_winter", Items.ACACIA_SAPLING);
        addCropGrowth("not_winter", Items.DARK_OAK_SAPLING);
        addCropGrowth("jungle", Items.BAMBOO);
        addCropGrowth("jungle", Blocks.BAMBOO_SAPLING);
        addSeasonalWeather(Season.SPRING, Weather.CLEAR, 70);
        addSeasonalWeather(Season.SPRING, Weather.RAIN, 30);
        addSeasonalWeather(Season.SUMMER, Weather.CLEAR, 95);
        addSeasonalWeather(Season.SUMMER, Weather.STORM, 3);
        addSeasonalWeather(Season.SUMMER, Weather.RAIN, 2);
        addSeasonalWeather(Season.AUTUMN, Weather.CLEAR, 50);
        addSeasonalWeather(Season.AUTUMN, Weather.RAIN, 40);
        addSeasonalWeather(Season.AUTUMN, Weather.FOG, 10);
        addSeasonalWeather(Season.WINTER, Weather.RAIN, 50);
        addSeasonalWeather(Season.WINTER, Weather.CLEAR, 40);
        addSeasonalWeather(Season.WINTER, Weather.STORM, 5);
        addSeasonalWeather(Season.WINTER, Weather.FOG, 5);
        addRandomWeather(World.OVERWORLD, Weather.CLEAR, 65);
        addRandomWeather(World.OVERWORLD, Weather.RAIN, 25);
        addRandomWeather(World.OVERWORLD, Weather.STORM, 5);
        addRandomWeather(World.OVERWORLD, Weather.FOG, 5);
        addWeatheredWorld(World.OVERWORLD, "seasonal", Weather.CLEAR, 24000, 1);
    }

    private void addCropGrowth(String predicate, Block item) {
        addCropGrowth(predicate, item.getRegistryName());
    }

    private void addCropGrowth(String predicate, Item item) {
        addCropGrowth(predicate, item.getRegistryName());
    }

    private void addCropGrowth(String predicate, ResourceLocation item) {
        addEntry("growth_seasons", "Item/Block,Season Predicate", CSVUtils.join(Objects.requireNonNull(item).toString(), predicate));
    }

    private void addSeasonPredicate(String name, Season season) {
        addEntry("season_predicates", "Name,Season", CSVUtils.join(name, season.name().toLowerCase(Locale.ROOT)));
    }

    private void addSeasonToWorld(RegistryKey<World> world, Season... seasons) {
        addEntry("seasonal_worlds", "World,Seasons", CSVUtils.join(world.location().toString(),
                CSVUtils.join(Lists.newArrayList(seasons).stream().map(s -> s.name().toLowerCase(Locale.ROOT)).toArray())));
    }

    private void addSeasonData(Season season, TextFormatting color, double temperature, String leaves, String grass, String sky, int sunrise, int sunset) {
        addEntry("seasons_data", "Season,HUD,Temperature,Leaves,Grass,Sky,Sunrise,Sunset",
                CSVUtils.join(season.name().toLowerCase(Locale.ROOT), color.getName(), temperature, leaves, grass, sky, sunrise, sunset));
    }

    private void addSeasonalWeather(Season season, Weather weather, int weight) {
        addEntry("seasonal_weather", "Season,Weather,Weight", CSVUtils.join(season.name().toLowerCase(Locale.ROOT), weather.name().toLowerCase(Locale.ROOT), weight));
    }

    private void addWeatheredWorld(RegistryKey<World> world, String type, Weather weather, int frequency, int chance) {
        addEntry("weathered_worlds", "World,Type,Default Weather,Frequency,Chance", CSVUtils.join(world.location().toString(), type, weather, frequency, chance));
    }

    private void addRandomWeather(RegistryKey<World> world, Weather weather, int weight) {
        addEntry("random_weather", "World,Weather,Weight", CSVUtils.join(world.location().toString(), weather.name().toLowerCase(Locale.ROOT), weight));
    }
}