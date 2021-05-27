package uk.joshiejack.simplyseasons.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshiejack.penguinlib.util.helpers.minecraft.TimeHelper;
import uk.joshiejack.simplyseasons.SimplySeasons;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.client.SSClient;
import uk.joshiejack.simplyseasons.world.date.CalendarDate;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SimplySeasons.MODID)
public class HUDRenderer {
    public static Object2ObjectMap<RegistryKey<World>, HUDRenderData> RENDERERS = new Object2ObjectOpenHashMap<>();
    public static boolean CLOCK_24H = true;

    static {
        RENDERERS.put(World.OVERWORLD, new SeasonsHUDRender()); //TODO: Combine with seasonal worlds?
    }

    public abstract static class HUDRenderData {
        public abstract ResourceLocation getTexture(Minecraft mc, Season season, CalendarDate date);
        public abstract ITextComponent getHeader(Minecraft mc, Season season, CalendarDate date);
    }

    private static String formatTime(int time) {
        int hour = time / 1000;
        int minute = (int) ((double) (time % 1000) / 20 * 1.2);
        if (CLOCK_24H) {
            return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
        } else {
            boolean pm = false;
            if (hour > 12) {
                hour = hour - 12;
                pm = true;
            }
            if (hour == 12)
                pm = true;
            if (hour == 0)
                hour = 12;

            return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + (pm ? "PM" : "AM");
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            HUDRenderData data = RENDERERS.get(mc.level.dimension());
            if (data != null) {
                MatrixStack matrix = event.getMatrixStack();
                RenderSystem.enableBlend();
                CalendarDate date = SSClient.INSTANCE.getDate();
                Season season = SSClient.INSTANCE.getSeason();
                if (date != null && season != null) {
                    int x = 0;
                    int y = 0;
                    RenderSystem.color4f(1F, 1F, 1F, 1F);
                    mc.getTextureManager().bind(data.getTexture(mc, season, date));//inMine ? MINE_HUD : season.HUD);
                    mc.gui.blit(matrix, x - 44, y - 35, 0, 0, 256, 110);

                    //Enlarge the Day
                    matrix.pushPose();
                    matrix.scale(1.4F, 1.4F, 1.4F);
                    ITextComponent header = data.getHeader(mc, season, date);
                    mc.font.drawShadow(matrix, header, (x / 1.4F) + 30, (y / 1.4F) + 7, 0xFFFFFFFF);
                    matrix.popPose();

                    //Draw the time
                    String time = formatTime((int) TimeHelper.getTimeOfDay(mc.level.getDayTime()));
                    mc.font.drawShadow(matrix, "(" + TimeHelper.shortName(date.getWeekday()) + ")" + "  " + time, x + 42, y + 23, 0xFFFFFFFF);
                }

                RenderSystem.disableBlend();
            }
        }
    }
}
