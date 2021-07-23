package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.client.Minecraft;
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
    public static final CalendarDate DATE = new CalendarDate();
    private boolean hasInInventory;

    private static final EnumMap<Season, ResourceLocation> HUD = new EnumMap<>(Season.class);
    static {
        registerSeasonHUD(Season.SPRING);
        registerSeasonHUD(Season.SUMMER);
        registerSeasonHUD(Season.AUTUMN);
        registerSeasonHUD(Season.WINTER);
    }

    private static final EnumMap<Season, TranslationTextComponent> TEXT_CACHE = new EnumMap<>(Season.class);

    private static void registerSeasonHUD(Season season) {
        HUD.put(season, new ResourceLocation(SimplySeasons.MODID, "textures/gui/" + season.name().toLowerCase(Locale.ENGLISH) + ".png"));
    }

    @Nullable
    public static Season getSeason(World world) {
        LazyOptional<ISeasonProvider> seasonsProvider = world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY);
        if (seasonsProvider.isPresent()) {
            return seasonsProvider.resolve().get().getSeason(world);
        } else return null;
    }

    private final Lazy<Ingredient> CALENDAR = Lazy.of(() -> Ingredient.of(CALENDARS));

    private boolean hasCalendarInInventory(PlayerEntity player) {
        if (player.level.getDayTime() % 60 == 0) {
            try {
                hasInInventory = PlayerHelper.hasInInventory(player, CALENDAR.get(), 1);
            } catch (Exception ex) { hasInInventory = false; }
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
        return HUD.get(getSeason(mc.level));
    }

    public static TranslationTextComponent getName(Season season) {
        return TEXT_CACHE.computeIfAbsent(season, (s) -> StringHelper.localize(SimplySeasons.MODID + "." + s.name().toLowerCase(Locale.ROOT)));
    }

    @Override
    public ITextComponent getHeader(Minecraft mc) {
        Season season = getSeason(mc.level);
        SeasonData data = SeasonData.get(season);
        if (mc.level.getDayTime() %60 == 0 || !SeasonsHUDRender.DATE.isSet()) SeasonsHUDRender.DATE.update(mc.level);
        return StringHelper.format(SimplySeasons.MODID + ".hud", getName(season), DATE.getDay()).withStyle(data.hud);
    }
}