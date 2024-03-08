package uk.joshiejack.simplyseasons.plugin.kubejs;

import dev.latvian.mods.kubejs.util.ListJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class SeasonUtils {
    public static boolean hasSeasons(Level worldJS) {
        return SSeasonsAPI.instance().getSeasonProvider(worldJS.dimension()).isPresent();
    }

    @Nullable
    public static List<?> getSeasonsAt(Level worldJS, BlockPos pos) {
        Optional<ISeasonProvider> provider = SSeasonsAPI.instance().getSeasonProvider(worldJS.dimension());
        return provider.<List<?>>map(iSeasonProvider -> ListJS.of(iSeasonProvider.getSeasonsAt(worldJS, pos))).orElse(null);
    }

    @Nullable
    public static Season getSeason(Level worldJS) {
        Optional<ISeasonProvider> provider = SSeasonsAPI.instance().getSeasonProvider(worldJS.dimension());
        return provider.map(iSeasonProvider -> iSeasonProvider.getSeason(worldJS)).orElse(null);
    }

    public static boolean seasonIs(Level worldJS, String name) {
        try {
            return seasonIs(worldJS, Season.valueOf(name.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) { return false; }
    }

    public static boolean seasonIs(Level worldJS, Season season) {
        return getSeason(worldJS) == season;
    }
}
