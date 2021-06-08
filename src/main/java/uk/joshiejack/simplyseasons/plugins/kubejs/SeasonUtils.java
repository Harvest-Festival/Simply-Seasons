package uk.joshiejack.simplyseasons.plugins.kubejs;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import javax.annotation.Nullable;
import java.util.Locale;

public class SeasonUtils {
    public static boolean hasSeasons(WorldJS worldJS) {
        return worldJS.minecraftWorld.getCapability(SSeasonsAPI.SEASONS_CAPABILITY).isPresent();
    }

    @Nullable
    public static ListJS getSeasonsAt(WorldJS worldJS, BlockPos pos) {
        LazyOptional<ISeasonProvider> provider = worldJS.minecraftWorld.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        return !provider.isPresent() ? null : ListJS.of(Lists.newArrayList(provider.resolve().get().getSeasonsAt(worldJS.minecraftWorld, pos)));
    }

    @Nullable
    public static Season getSeason(WorldJS worldJS) {
        LazyOptional<ISeasonProvider> provider = worldJS.minecraftWorld.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        return !provider.isPresent() ? null : provider.resolve().get().getSeason(worldJS.minecraftWorld);
    }

    public static boolean seasonIs(WorldJS worldJS, String name) {
        try {
            return seasonIs(worldJS, Season.valueOf(name.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) { return false; }
    }

    public static boolean seasonIs(WorldJS worldJS, Season season) {
        return getSeason(worldJS) == season;
    }
}
