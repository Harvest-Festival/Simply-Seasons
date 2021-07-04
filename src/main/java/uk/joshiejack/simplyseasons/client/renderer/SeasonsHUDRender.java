package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import uk.joshiejack.penguinlib.client.gui.HUDRenderer;
import uk.joshiejack.penguinlib.util.helpers.generic.StringHelper;
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
    public static final CalendarDate DATE = new CalendarDate();
    private static final EnumMap<Season, ResourceLocation> HUD = new EnumMap<>(Season.class);
    static {
        registerSeasonHUD(Season.SPRING);
        registerSeasonHUD(Season.SUMMER);
        registerSeasonHUD(Season.AUTUMN);
        registerSeasonHUD(Season.WINTER);
    }

    private static EnumMap<Season, TranslationTextComponent> TEXT_CACHE = new EnumMap<>(Season.class);

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

    @Override
    public boolean isEnabled() {
        return SSClientConfig.enableHUD.get() && getSeason(Minecraft.getInstance().level) != null;
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