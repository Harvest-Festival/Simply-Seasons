package uk.joshiejack.simplyseasons.loot;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.events.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SeasonPredicate {
    public static final SeasonPredicate ANY = new SeasonPredicate(Sets.newEnumSet(Sets.newHashSet(Season.values()), Season.class));
    public static final Map<String, SeasonPredicate> REGISTRY = Maps.newHashMap();
    private final EnumSet<Season> seasons;

    public SeasonPredicate(@Nonnull EnumSet<Season> seasons) {
        this.seasons = seasons;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) //Many other things use these predicates, so make sure they're loaded
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        REGISTRY.clear(); //Reloading, so clear the map
        event.table("season_predicates").rows().forEach(row -> {
            String name = row.name();
            Season season = Season.valueOf(row.get("season").toString().toUpperCase(Locale.ENGLISH));
            if (!SeasonPredicate.REGISTRY.containsKey(name))
                SeasonPredicate.REGISTRY.put(name, new SeasonPredicate(EnumSet.noneOf(Season.class)));
            SeasonPredicate.REGISTRY.get(name).seasons.add(season); //Add the new seasons
        });
    }

    public boolean matches(ServerWorld world, BlockPos pos) {
        return matches(world, pos.getX(), pos.getY(), pos.getZ());
    }

    @SuppressWarnings("ConstantConditions")
    public boolean matches(ServerWorld world, double x, double y, double z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        LazyOptional<ISeasonsProvider> seasonsProvider = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        return seasonsProvider.isPresent() && seasonsProvider.resolve().get().getSeasonsAt(world, blockpos).stream()
                .anyMatch(season -> seasons.stream()
                        .anyMatch(s2 -> s2 == season));
    }

    public JsonElement serializeToJson() {
        if (this == ANY)
            return JsonNull.INSTANCE;
        else {
            JsonObject jsonobject = new JsonObject();
            if (!this.seasons.isEmpty()) {
                JsonArray array = new JsonArray();
                for (Season season : seasons)
                    array.add(season.name().toLowerCase(Locale.ROOT));
                jsonobject.add("seasons", array);
            }

            return jsonobject;
        }
    }

    public static SeasonPredicate fromJson(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            if (json.getAsJsonObject().has("seasons")) {
                Iterator<JsonElement> it = json.getAsJsonObject().getAsJsonArray("seasons").iterator();
                EnumSet<Season> set = EnumSet.noneOf(Season.class);
                while (it.hasNext()) {
                    set.add(Season.valueOf(it.next().getAsString().toUpperCase(Locale.ROOT)));
                }

                return new SeasonPredicate(set);
            }
        }

        return ANY;
    }

    public static class Builder {
        private EnumSet<Season> seasons = EnumSet.noneOf(Season.class);

        public static Builder season() {
            return new Builder();
        }

        public Builder addSeason(@Nonnull Season season) {
            seasons.add(season);
            return this;
        }

        public SeasonPredicate build() {
            return new SeasonPredicate(seasons);
        }
    }
}