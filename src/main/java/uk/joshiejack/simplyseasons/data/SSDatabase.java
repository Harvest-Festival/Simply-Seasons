package uk.joshiejack.simplyseasons.data;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import uk.joshiejack.penguinlib.data.database.CSVUtils;
import uk.joshiejack.penguinlib.data.generators.AbstractDatabaseProvider;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.weather.Weather;

import java.util.Locale;
import java.util.Objects;

public class SSDatabase extends AbstractDatabaseProvider {
    public SSDatabase(DataGenerator gen) {
        super(gen, SimplySeasons.MODID);
    }

    @Override
    protected void addDatabaseEntries() {
        addTimeUnit("season_length_multiplier", 1);
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
        addCropGrowth("not_winter", Blocks.WHEAT);
        addCropGrowth("autumn", Blocks.CARROTS);
        addCropGrowth("spring", Blocks.POTATOES);
        addCropGrowth("autumn", Blocks.BEETROOTS);
        addCropGrowth("late", Blocks.MELON_STEM);
        addCropGrowth("late", Blocks.PUMPKIN_STEM);
        addCropGrowth("jungle", Blocks.COCOA);
        addCropGrowth("jungle", Blocks.SUGAR_CANE);
        addCropGrowth("jungle", Blocks.BAMBOO);
        addCropGrowth("desert", Blocks.CACTUS);
        addCropGrowth("late", Blocks.SWEET_BERRY_BUSH);
        addCropGrowth("not_winter", Blocks.OAK_SAPLING);
        addCropGrowth("not_winter", Blocks.SPRUCE_SAPLING);
        addCropGrowth("not_winter", Blocks.BIRCH_SAPLING);
        addCropGrowth("jungle", Blocks.JUNGLE_SAPLING);
        addCropGrowth("not_winter", Blocks.ACACIA_SAPLING);
        addCropGrowth("not_winter", Blocks.DARK_OAK_SAPLING);
        addCropGrowth("jungle", Blocks.BAMBOO_SAPLING);
        addWeather(Season.SPRING, Weather.CLEAR, 70);
        addWeather(Season.SPRING, Weather.RAIN, 30);
        addWeather(Season.SUMMER, Weather.CLEAR, 95);
        addWeather(Season.SUMMER, Weather.STORM, 3);
        addWeather(Season.SUMMER, Weather.RAIN, 2);
        addWeather(Season.AUTUMN, Weather.CLEAR, 55);
        addWeather(Season.AUTUMN, Weather.RAIN, 45);
        addWeather(Season.WINTER, Weather.CLEAR, 50);
        addWeather(Season.WINTER, Weather.STORM, 5);
        addWeather(Season.WINTER, Weather.RAIN, 45);
    }

    private void addCropGrowth(String predicate, Block block) {
        addEntry("growth_seasons", "Block,Season Predicate", CSVUtils.join(Objects.requireNonNull(block.getRegistryName()).toString(), predicate));
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

    private void addWeather(Season season, Weather weather, int weight) {
        addEntry("weather", "Season,Weather,Weight", CSVUtils.join(season.name().toLowerCase(Locale.ROOT), weather.name().toLowerCase(Locale.ROOT), weight));
    }
}