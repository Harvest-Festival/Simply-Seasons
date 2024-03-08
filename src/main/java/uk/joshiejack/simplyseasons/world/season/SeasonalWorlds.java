package uk.joshiejack.simplyseasons.world.season;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import uk.joshiejack.penguinlib.data.database.CSVUtils;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.plugin.SereneSeasonsPlugin;
import uk.joshiejack.simplyseasons.plugin.SereneSeasonsSeasonProvider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SeasonalWorlds {
    private static final Map<ResourceKey<Level>, ISeasonProvider> PROVIDERS = new HashMap<>();
    public static final TagKey<Block> SPRING_NO_BONE_MEAL_BLOCKS = BlockTags.create(new ResourceLocation(SimplySeasons.MODID, "spring_no_bone_meal"));


    public static void setSeasonalWorld(ResourceKey<Level> dimension, SereneSeasonsSeasonProvider provider) {
        PROVIDERS.put(dimension, provider);
    }

    public static Optional<ISeasonProvider> getSeasonProvider(ResourceKey<Level> level) {
        return Optional.ofNullable(PROVIDERS.get(level));
    }

    @SubscribeEvent
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        if (SereneSeasonsPlugin.loaded)
            return; //They will handle which worlds have seasons
        PROVIDERS.clear(); //Clear out the existing providers, as we're reloading
        event.table("seasonal_worlds").rows().forEach(row -> {
            ResourceKey<Level> world = ResourceKey.create(Registries.DIMENSION, row.getRL("world"));
            PROVIDERS.put(world, new SeasonsProvider(CSVUtils.parse(row.get("seasons")).stream()
                    .map(string -> Season.valueOf(string.toUpperCase(Locale.ROOT)))
                    .filter(season -> season != Season.WET && season != Season.DRY)
                    .toArray(Season[]::new)));
        });
    }

    @SuppressWarnings("deprecation")
    public static float getTemperature(LevelReader reader, Biome biome, BlockPos pos) {
        Level world = reader instanceof WorldGenRegion ? ((WorldGenRegion) reader).getLevel() : reader instanceof Level ? (Level) reader : null;
        if (world != null) {
            ISeasonProvider provider = getSeasonProvider(world.dimension()).orElse(null);
            if (provider != null) {
                return biome.getTemperature(pos) + SeasonData.get(provider.getSeason(world)).temperature();
            }
        }

        return biome.getTemperature(pos);
    }

    public static float getTemperature(LevelReader reader, Holder<Biome> biome, BlockPos pos) {
        return getTemperature(reader, biome.value(), pos);
    }

    public static boolean shouldMelt(BlockState state, float temperature, Block block) {
        return temperature >= 0.15F && state.getBlock() == block;
    }

    public static boolean warmEnoughToRain(LevelReader world, Biome biome, BlockPos pos) {
        return getTemperature(world, biome, pos) >= 0.15F;
    }

    public static Biome.Precipitation getPrecipitationAt(LevelReader level, Biome biome, BlockPos pos) {
        return biome.hasPrecipitation() ? warmEnoughToRain(level, biome, pos) ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW : Biome.Precipitation.NONE;
    }
}