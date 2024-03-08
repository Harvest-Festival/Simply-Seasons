package uk.joshiejack.simplyseasons.world.loot;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = SimplySeasons.MODID)
public class SeasonPredicate implements LootItemCondition {
    public static final SeasonPredicate ANY = new SeasonPredicate(new ArrayList<>());
    public static final Map<String, SeasonPredicate> REGISTRY = Maps.newHashMap();
    private final List<Season> seasons;

    public SeasonPredicate(@Nonnull List<Season> seasons) {
        this.seasons = seasons;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    //Many other things use these predicates, so make sure they're loaded
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        REGISTRY.clear(); //Reloading, so clear the map
        event.table("season_predicates").rows().forEach(row -> {
            String name = row.name();
            Season season = Season.valueOf(row.get("season").toString().toUpperCase(Locale.ENGLISH));
            if (!SeasonPredicate.REGISTRY.containsKey(name))
                SeasonPredicate.REGISTRY.put(name, new SeasonPredicate(new ArrayList<>()));
            SeasonPredicate.REGISTRY.get(name).seasons.add(season); //Add the new seasons
        });
    }

    public Stream<Season> seasons() {
        return seasons.stream();
    }

    public boolean matches(Level world, BlockPos pos) {
        return matches(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean matches(Level world, double x, double y, double z) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        ISeasonProvider seasonsProvider = SSeasonsAPI.instance().getSeasonProvider(world.dimension()).orElse(null);
        return seasonsProvider != null && seasonsProvider.getSeasonsAt(world, blockpos).stream()
                .anyMatch(season -> seasons.stream()
                        .anyMatch(s2 -> s2 == season));
    }

    //Switch to codec
    public static final Codec<SeasonPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Season.CODEC).optionalFieldOf("seasons", ANY.seasons).forGetter(predicate -> predicate.seasons)
    ).apply(instance, SeasonPredicate::new));

    @Override
    public @NotNull LootItemConditionType getType() {
        return SimplySeasons.SSLoot.SEASON.value();
    }

    @Override
    public boolean test(LootContext ctx) {
        Vec3 v3d = ctx.getParamOrNull(LootContextParams.ORIGIN);
        return v3d != null && this.matches(ctx.getLevel(), v3d.x, v3d.y, v3d.z);
    }


    public static class Builder {
        private final List<Season> seasons = new ArrayList<>();

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
