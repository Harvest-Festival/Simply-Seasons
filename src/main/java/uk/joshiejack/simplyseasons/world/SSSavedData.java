package uk.joshiejack.simplyseasons.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.world.season.SeasonsProvider;
import uk.joshiejack.simplyseasons.world.weather.AbstractWeatherProvider;

import java.util.Optional;

public class SSSavedData extends SavedData {
    private final ResourceKey<Level> level;

    public SSSavedData(Level level) {
        this.level = level.dimension();
    }

    public static SSSavedData load(Level level, CompoundTag tag) {
        SSSavedData data = new SSSavedData(level);
        if (tag.contains("Weather")) {
            Optional<IWeatherProvider> provider = SSeasonsAPI.instance().getWeatherProvider(level.dimension());
            provider.ifPresent(p -> {
                if (p instanceof AbstractWeatherProvider) {
                    ((AbstractWeatherProvider) p).load(tag.getCompound("Weather"));
                }
            });
        }

        if (tag.contains("Seasons")) {
            Optional<ISeasonProvider> seasonProvider = SSeasonsAPI.instance().getSeasonProvider(level.dimension());
            seasonProvider.ifPresent(p -> {
                if (p instanceof SeasonsProvider) {
                    ((SeasonsProvider) p).load(tag.getCompound("Seasons"));
                }
            });
        }

        return data;
    }

    public static SSSavedData get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(() -> new SSSavedData(world), (tag) -> SSSavedData.load(world, tag)), "mines");
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        CompoundTag weather = new CompoundTag();
        CompoundTag seasons = new CompoundTag();
        Optional<IWeatherProvider> provider = SSeasonsAPI.instance().getWeatherProvider(level);
        provider.ifPresent(p -> {
            if (p instanceof AbstractWeatherProvider) {
                ((AbstractWeatherProvider) p).save(weather);
            }
        });

        Optional<ISeasonProvider> seasonProvider = SSeasonsAPI.instance().getSeasonProvider(level);
        seasonProvider.ifPresent(p -> {
            if (p instanceof SeasonsProvider) {
                ((SeasonsProvider) p).save(seasons);

            }
        });

        if (!weather.isEmpty())
            tag.put("Weather", weather);
        if (!seasons.isEmpty())
            tag.put("Seasons", seasons);

        return tag;
    }
}
