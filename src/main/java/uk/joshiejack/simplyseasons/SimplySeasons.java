package uk.joshiejack.simplyseasons;

import net.minecraft.DetectedVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.client.SSClientConfig;
import uk.joshiejack.simplyseasons.data.*;
import uk.joshiejack.simplyseasons.world.SSServerConfig;
import uk.joshiejack.simplyseasons.world.loot.SeasonPredicate;
import uk.joshiejack.simplyseasons.world.season.SeasonalCrops;
import uk.joshiejack.simplyseasons.world.season.SeasonalWorlds;
import uk.joshiejack.simplyseasons.world.weather.WeatheredWorlds;

import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(SimplySeasons.MODID)
public class SimplySeasons implements SSeasonsAPI.Info {
    public static final String MODID = "simplyseasons";
    public static SSeasonsAPI.Info API;

    public SimplySeasons(IEventBus eventBus) {
        API = this; //Load in the api
        SSSounds.SOUNDS.register(eventBus);
        SSLoot.LOOT_CONDITION_TYPES.register(eventBus);
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, SSConfig.create());
        ctx.registerConfig(ModConfig.Type.CLIENT, SSClientConfig.create());
        ctx.registerConfig(ModConfig.Type.SERVER, SSServerConfig.create());
    }

    @SubscribeEvent
    public static void onDataGathering(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = event.getGenerator().getPackOutput();
        generator.addProvider(event.includeClient(), new SSLanguage(output));

        generator.addProvider(event.includeServer(), new SSDatabase(output));
        SSBlockTags blocktags = new SSBlockTags(output, event.getLookupProvider(), event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blocktags);
        generator.addProvider(event.includeServer(), new SSItemTags(output, event.getLookupProvider(), blocktags.contentsGetter(), event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new SSDataMapProvider(output, event.getLookupProvider()));

        generator.addProvider(true, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Resources for Simply Seasons"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
                Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));
    }

    @Override
    public Optional<ISeasonProvider> getSeasonProvider(ResourceKey<Level> level) {
        return SeasonalWorlds.getSeasonProvider(level);
    }

    @Override
    public Optional<IWeatherProvider> getWeatherProvider(ResourceKey<Level> level) {
        return WeatheredWorlds.getWeatherProvider(level);
    }

    @Override
    public boolean canGrow(Level level, BlockPos pos, BlockState state) {
        SeasonPredicate predicate = SeasonalCrops.blockPredicate(state);
        return predicate == null || predicate.matches(level, pos);
    }

    @Override
    public boolean applyOutOfSeasonEffect(Level level, BlockPos pos) {
        return SSConfig.cropOutOfSeasonEffect.get().predicate.test(level, pos);
    }

    public static class SSSounds {
        public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, SimplySeasons.MODID);
        public static final Holder<SoundEvent> SPRING = createSoundEvent("music.spring");
        public static final Holder<SoundEvent> SUMMER = createSoundEvent("music.summer");
        public static final Holder<SoundEvent> AUTUMN = createSoundEvent("music.autumn");
        public static final Holder<SoundEvent> WINTER = createSoundEvent("music.winter");
        public static final Holder<SoundEvent> BLIZZARD = createSoundEvent("blizzard");

        private static DeferredHolder<SoundEvent, SoundEvent> createSoundEvent(String name) {
            return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, name)));
        }
    }

    public static class SSLoot {
        public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MODID);
        public static final Holder<LootItemConditionType> SEASON = LOOT_CONDITION_TYPES.register("season", () -> new LootItemConditionType(SeasonPredicate.CODEC));
    }

    public static class SSConfig {
        public static ModConfigSpec.EnumValue<SeasonalCrops.CropOutOfSeasonEffect> cropOutOfSeasonEffect;
        public static ModConfigSpec.BooleanValue disableOutofSeasonPlanting;
        public static ModConfigSpec.BooleanValue enableBeeInactivityInWinter;
        public static ModConfigSpec.IntValue noSleepingBefore;
        public static ModConfigSpec.BooleanValue noSleepingBetweenWakeupAndSunset;
        public static ModConfigSpec.BooleanValue wakePlayerUpAtSunrise;
        public static ModConfigSpec.BooleanValue sleepAtAnytime;

        SSConfig(ModConfigSpec.Builder builder) {
            builder.push("Seasons");
            cropOutOfSeasonEffect = builder.defineEnum("Crop out of season effect", SeasonalCrops.CropOutOfSeasonEffect.REPLACE_WITH_JUNK);
            disableOutofSeasonPlanting = builder.define("Disable planting of seeds that are out of season", true);
            enableBeeInactivityInWinter = builder.define("Enable inactive bees in winter", true);

            builder.pop();
            builder.push("Sleeping");
            noSleepingBefore = builder.comment("5999 = Ignore this. 10000 = 10AM. 13000 = 1PM etc.").defineInRange("Disable sleeping between 6:00 and the specified time", 5999, 5999, 23999);
            noSleepingBetweenWakeupAndSunset = builder.define("Disable sleeping between waking up and sunset", true);
            wakePlayerUpAtSunrise = builder.define("Wake up at sunrise", false);
            sleepAtAnytime = builder.define("Sleep at any time", false);
            builder.pop();
        }

        public static ModConfigSpec create() {
            return new ModConfigSpec.Builder().configure(SSConfig::new).getValue();
        }
    }
}