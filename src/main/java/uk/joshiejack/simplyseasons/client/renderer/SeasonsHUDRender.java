package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.Lazy;
import uk.joshiejack.penguinlib.client.gui.HUDRenderer;
import uk.joshiejack.penguinlib.util.helper.PlayerHelper;
import uk.joshiejack.penguinlib.util.helper.StringHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClientConfig;
import uk.joshiejack.simplyseasons.world.CalendarDate;
import uk.joshiejack.simplyseasons.world.season.SeasonData;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused, ConstantConditions")
public class SeasonsHUDRender extends HUDRenderer.HUDRenderData {
    public static final TagKey<Item> CALENDARS = ItemTags.create(new ResourceLocation(SimplySeasons.MODID, "calendars"));
    private static final EnumMap<Season, Component> TEXT_CACHE = new EnumMap<>(Season.class);
    private final EnumMap<Season, ResourceLocation> textures = new EnumMap<>(Season.class);
    private final Lazy<Ingredient> calendar = Lazy.of(() -> Ingredient.of(CALENDARS));
    private final CalendarDate date = new CalendarDate();
    private boolean hasInInventory;

    public SeasonsHUDRender() {
        for (Season season : Season.MAIN)
            textures.put(season, new ResourceLocation(SimplySeasons.MODID, "textures/gui/" + season.name().toLowerCase(Locale.ENGLISH) + ".png"));
    }

    public void recalculateDate(ClientLevel level) {
        date.update(level);
    }

    @Nullable
    public static Season getSeason(Level world) {
        ISeasonProvider provider = SSeasonsAPI.instance().getSeasonProvider(world.dimension()).orElse(null);
        return provider != null ? provider.getSeason(world) : null;
    }


    private boolean hasCalendarInInventory(Player player) {
        if (player.level().getDayTime() % 60 == 0) { //TODO: Check if getDayTimeIsCorrect
            try {
                hasInInventory = PlayerHelper.hasInInventory(player, calendar.get(), 1);
            } catch (Exception ex) {
                hasInInventory = false;
            }
        }

        return hasInInventory;
    }

    @Override
    public boolean isEnabled(Minecraft mc) {
        return SSClientConfig.enableHUD.get() && getSeason(Minecraft.getInstance().level) != null
                && (!SSClientConfig.requireItemInInventoryForHUD.get() || (SSClientConfig.requireItemInInventoryForHUD.get() &&
                hasCalendarInInventory(Minecraft.getInstance().player)));
    }

    @Override
    public ResourceLocation getTexture(Minecraft mc) {
        return textures.get(getSeason(mc.level));
    }

    public static Component getName(Season season) {
        return TEXT_CACHE.computeIfAbsent(season, (s) -> StringHelper.localize(SimplySeasons.MODID + "." + s.name().toLowerCase(Locale.ROOT)));
    }

    @Override
    public Component getHeader(Minecraft mc) {
        Season season = getSeason(mc.level);
        SeasonData data = SeasonData.get(season);
        if (mc.level.getDayTime() % 60 == 0 || !date.isSet()) date.update(mc.level);
        return StringHelper.format(SimplySeasons.MODID + ".hud", getName(season), date.getDay()).copy().withStyle(data.hud());
    }

    @Override
    public int getClockX() {
        return isEnabled(Minecraft.getInstance()) ? super.getClockX() : 8;
    }

    @Override
    public int getClockY() {
        return isEnabled(Minecraft.getInstance()) ? super.getClockY() : 5;
    }
}