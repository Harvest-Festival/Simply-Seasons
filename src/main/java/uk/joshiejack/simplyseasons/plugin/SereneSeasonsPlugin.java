package uk.joshiejack.simplyseasons.plugin;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import uk.joshiejack.penguinlib.util.IModPlugin;
import uk.joshiejack.penguinlib.util.registry.Plugin;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.world.SSServerConfig;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.IntStream;

@Plugin("sereneseasons")
public class SereneSeasonsPlugin implements IModPlugin {
    public static final TagKey<Block> GREENHOUSE_GLASS = BlockTags.create(new ResourceLocation("sereneseasons:greenhouse_glass"));
    public static boolean loaded;
    private final SereneSeasonsSeasonProvider provider;

    public SereneSeasonsPlugin() {
        this.provider = new SereneSeasonsSeasonProvider();
    }

    @Override
    public void setup() {
        //Register season providers, using serene seasons data
        SereneSeasonsPlugin.loaded = true;
        NeoForge.EVENT_BUS.register(this);
        final Set<Season> all = Sets.newHashSet(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER, Season.WET, Season.DRY);
        final Set<Season> none = Sets.newHashSet();
        SSeasonsAPI.LOCALIZED_SEASON_HANDLER.add((world, pos) -> SSServerConfig.useSSCropsHandler.get()
                && IntStream.rangeClosed(0, 15)
                .anyMatch(i -> world.getBlockState(pos.offset(0, i + 1, 0)).is(GREENHOUSE_GLASS)) ? all : none);
    }

    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof net.minecraft.world.level.Level level && isWhitelisted(level.dimension()))
            SeasonalWorlds.setSeasonalWorld(level.dimension(), provider);
    }

    private boolean isWhitelisted(ResourceKey<Level> world) {
        try {
            Field field = ObfuscationReflectionHelper.findField(Class.forName(
                    "sereneseasons.init.ModConfig"),
                    "seasons");
            Method method = ObfuscationReflectionHelper.findMethod(Class.forName(
                    "sereneseasons.config.SeasonsConfig"),
                    "isDimensionWhitelisted", ResourceKey.class);
            return (boolean) method.invoke(field.get(null), world);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }
}