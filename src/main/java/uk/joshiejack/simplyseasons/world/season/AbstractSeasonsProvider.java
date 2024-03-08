package uk.joshiejack.simplyseasons.world.season;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.network.SeasonChangedPacket;
import uk.joshiejack.simplyseasons.world.CalendarDate;

public abstract class AbstractSeasonsProvider implements ISeasonProvider {
    private Season previousSeason;
    private long previousElapsed;

    @Override
    public int getDay(Level world) {
        return 1 + CalendarDate.getDay(world);
    }

    @Override
    public void recalculate(Level world) {
        if (!world.isClientSide) {
            Season season = getSeason(world);
            long elapsed = TimeHelper.getElapsedDays(world.getDayTime());
            if (elapsed != previousElapsed || previousSeason != season)
                PenguinNetwork.sendToDimension(new SeasonChangedPacket(season, previousSeason != season), (ServerLevel) world);
            previousElapsed = elapsed;
            previousSeason = getSeason(world);
        }
    }
}
