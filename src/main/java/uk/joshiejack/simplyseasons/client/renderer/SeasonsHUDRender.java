package uk.joshiejack.simplyseasons.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.helpers.generic.StringHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.season.SeasonData;
import uk.joshiejack.simplyseasons.world.CalendarDate;

import java.util.EnumMap;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class SeasonsHUDRender extends HUDRenderer.HUDRenderData {
    private static final EnumMap<Season, ResourceLocation> HUD = new EnumMap<>(Season.class);
    static {
        registerSeasonHUD(Season.SPRING);
        registerSeasonHUD(Season.SUMMER);
        registerSeasonHUD(Season.AUTUMN);
        registerSeasonHUD(Season.WINTER);
    }

    private static void registerSeasonHUD(Season season) {
        HUD.put(season, new ResourceLocation(SimplySeasons.MODID, "textures/gui/" + season.name().toLowerCase(Locale.ENGLISH) + ".png"));
    }

    @Override
    public ResourceLocation getTexture(Minecraft mc, Season season, CalendarDate date) {
        return HUD.get(season);
    }

    public TranslationTextComponent getName(Season season) {
        return StringHelper.localize(SimplySeasons.MODID + "." + season.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public ITextComponent getHeader(Minecraft mc, Season season, CalendarDate date) {
        SeasonData data = SeasonData.DATA.get(season);
        return StringHelper.format(SimplySeasons.MODID + ".hud", getName(season), date.getDay()).withStyle(data.hud);
    }
}
