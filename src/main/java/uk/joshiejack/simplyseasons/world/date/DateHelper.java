package uk.joshiejack.simplyseasons.world.date;

import uk.joshiejack.penguinlib.util.helpers.minecraft.TimeHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;

public class DateHelper {
    public static int getYear(long time) {
        return (int) Math.floor((double) TimeHelper.getElapsedDays(time) / 4 / SimplySeasons.DAYS_PER_SEASON);
    }

    public static int getDay(long totalTime) {
        return TimeHelper.getElapsedDays(totalTime) % SimplySeasons.DAYS_PER_SEASON;
    }
}