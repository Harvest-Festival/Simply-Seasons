package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import uk.joshiejack.penguinlib.client.gui.HUDRenderer;
import uk.joshiejack.penguinlib.util.helpers.PlayerHelper;
import uk.joshiejack.penguinlib.util.helpers.StringHelper;
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
    public static final ITag.INamedTag<Item> CALENDARS = ItemTags.createOptional(new ResourceLocation(SimplySeasons.MODID, "calendars"));
    private static final EnumMap<Season, TranslationTextComponent> TEXT_CACHE = new EnumMap<>(Season.class);
    private final EnumMap<Season, ResourceLocation> textures = new EnumMap<>(Season.class);
    private final Lazy<Ingredient> calendar = Lazy.of(() -> Ingredient.of(CALENDARS));
    private final CalendarDate date = new CalendarDate();
    private boolean hasInInventory;

    public SeasonsHUDRender() {
        for (Season season : Season.MAIN)
            textures.put(season, new ResourceLocation(SimplySeasons.MODID, "textures/gui/" + season.name().toLowerCase(Locale.ENGLISH) + ".png"));
    }

    public void recalculateDate(ClientWorld level) {
        date.update(level);
    }

    @Nullable
    public static Season getSeason(World world) {
        LazyOptional<ISeasonProvider> seasonsProvider = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (seasonsProvider.isPresent()) {
            return seasonsProvider.resolve().get().getSeason(world);
        } else return null;
    }


    private boolean hasCalendarInInventory(PlayerEntity player) {
        if (player.level.getDayTime() % 60 == 0) {
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

    public static TranslationTextComponent getName(Season season) {
        return TEXT_CACHE.computeIfAbsent(season, (s) -> StringHelper.localize(SimplySeasons.MODID + "." + s.name().toLowerCase(Locale.ROOT)));
    }

    @Override
    public ITextComponent getHeader(Minecraft mc) {
        Season season = getSeason(mc.level);
        SeasonData data = SeasonData.get(season);
        if (mc.level.getDayTime() % 60 == 0 || !date.isSet()) date.update(mc.level);
        return StringHelper.format(SimplySeasons.MODID + ".hud", getName(season), date.getDay()).withStyle(data.hud);
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