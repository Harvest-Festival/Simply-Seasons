package uk.joshiejack.simplyseasons.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import uk.joshiejack.penguinlib.data.TimeUnitRegistry;
import uk.joshiejack.penguinlib.util.helpers.TimeHelper;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;

import javax.annotation.Nonnull;
import java.time.DayOfWeek;
import java.util.Objects;

public class CalendarDate implements INBTSerializable<CompoundNBT> {
    public static final int DAYS_PER_SEASON = 7;
    public static boolean isSinglePlayer;
    private DayOfWeek weekday = DayOfWeek.MONDAY;
    private int monthday = 1;
    private int year = 1;
    private boolean set;

    public CalendarDate() {}
    public CalendarDate(DayOfWeek weekday, int monthday, int year) {
        this.weekday = weekday;
        this.monthday = monthday;
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
        return monthday;
    }

    public int getYear() {
        return year;
    }

    public void update(World world) {
        set = true;
        weekday = TimeHelper.getWeekday(world.getDayTime());
        LazyOptional<ISeasonProvider> optional = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (optional.isPresent() && optional.resolve().isPresent())
            monthday = optional.resolve().get().getDay(world);
        else monthday = 1 + getDay(world);
        year = 1 + getYear(world);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putByte("Weekday", (byte) getWeekday().ordinal());
        tag.putShort("Day", (short) monthday);
        tag.putShort("Year", (short) year);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        weekday = DayOfWeek.values()[tag.getByte("Weekday")];
        monthday = tag.getShort("Day");
        year = tag.getShort("Year");
    }

    public static int getYear(World world) {
        return (int) Math.floor((double) TimeHelper.getElapsedDays(world.getDayTime()) / 4 / seasonLength(world));
    }

    public static int getDay(World world) {
        return (int) (TimeHelper.getElapsedDays(world.getDayTime()) % seasonLength(world));
    }

    private static int serverTypeMultiplier(World world) {
        return (world.isClientSide && isSinglePlayer) ||
                (!world.isClientSide && Objects.requireNonNull(world.getServer()).isSingleplayer())
                ? 1 : (int) (TimeUnitRegistry.get("dedicated_server_season_multiplier"));
    }

    public static float seasonLength(World world) {
        return (float) (TimeUnitRegistry.get("season_length_multiplier") * DAYS_PER_SEASON * serverTypeMultiplier(world));
    }

    public boolean isSet() {
        return set;
    }
}
