package uk.joshiejack.simplyseasons.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import uk.joshiejack.penguinlib.data.TimeUnitRegistry;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;

import javax.annotation.Nonnull;
import java.time.DayOfWeek;
import java.util.Objects;

public class CalendarDate implements INBTSerializable<CompoundTag> {
    public static final int DAYS_PER_SEASON = 7;
    public static boolean isSinglePlayer;
    private DayOfWeek weekday = DayOfWeek.MONDAY;
    private int dayOfMonth = 1;
    private int year = 1;
    private boolean set;

    public CalendarDate() {}
    public CalendarDate(DayOfWeek weekday, int dayOfMonth, int year) {
        this.weekday = weekday;
        this.dayOfMonth = dayOfMonth;
        this.year = year;
    }

    @Nonnull
    public DayOfWeek getWeekday() {
        if (weekday == null) {
            weekday = DayOfWeek.MONDAY;
        }

        return weekday;
    }

    public int getDay() {
        return dayOfMonth;
    }

    public int getYear() {
        return year;
    }

    public void update(Level world) {
        set = true;
        weekday = TimeHelper.getWeekday(world.getDayTime()); //TODO: is this correct?
        ISeasonProvider optional = SSeasonsAPI.instance().getSeasonProvider(world.dimension()).orElse(null);
        if (optional != null)
            dayOfMonth = optional.getDay(world);
        else dayOfMonth = 1 + getDay(world);
        year = 1 + getYear(world);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("Weekday", (byte) getWeekday().ordinal());
        tag.putShort("Day", (short) dayOfMonth);
        tag.putShort("Year", (short) year);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        weekday = DayOfWeek.values()[tag.getByte("Weekday")];
        dayOfMonth = tag.getShort("Day");
        year = tag.getShort("Year");
    }

    public static int getYear(Level world, long time) {
        return (int) Math.floor((double) TimeHelper.getElapsedDays(time) / 4 / seasonLength(world));
    }

    public static int getYear(Level world) {
        return getYear(world, world.getDayTime());
    }

    public static int getDay(Level world) {
        return (int) (TimeHelper.getElapsedDays(world.getDayTime()) % seasonLength(world));
    }

    private static int serverTypeMultiplier(Level world) {
        return (world.isClientSide && isSinglePlayer) ||
                (!world.isClientSide && Objects.requireNonNull(world.getServer()).isSingleplayer())
                ? 1 : (int) (TimeUnitRegistry.get("dedicated_server_season_multiplier"));
    }

    public static float seasonLength(Level world) {
        return (float) (TimeUnitRegistry.get("season_length_multiplier") * DAYS_PER_SEASON * serverTypeMultiplier(world));
    }

    public boolean isSet() {
        return set;
    }
}
