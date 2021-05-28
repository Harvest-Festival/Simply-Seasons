package uk.joshiejack.simplyseasons.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import uk.joshiejack.penguinlib.util.helpers.minecraft.TimeHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;

import javax.annotation.Nonnull;
import java.time.DayOfWeek;

public class CalendarDate implements INBTSerializable<CompoundNBT> {
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

    public void update(long time) {
        set = true;
        weekday = TimeHelper.getWeekday(time);
        monthday = 1 + getDay(time);
        year = 1 + getYear(time);
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

    public static CalendarDate getFromTime(long time) {
        return new CalendarDate(TimeHelper.getWeekday(time), 1 + getDay(time), 1 + getYear(time));
    }

    public static int getYear(long time) {
        return (int) Math.floor((double) TimeHelper.getElapsedDays(time) / 4 / SimplySeasons.DAYS_PER_SEASON);
    }

    public static int getDay(long totalTime) {
        return TimeHelper.getElapsedDays(totalTime) % SimplySeasons.DAYS_PER_SEASON;
    }

    public boolean isSet() {
        return set;
    }
}
