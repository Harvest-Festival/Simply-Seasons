package uk.joshiejack.simplyseasons;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.IWeatherProvider;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.api.Weather;
import uk.joshiejack.simplyseasons.client.SSClientConfig;
import uk.joshiejack.simplyseasons.data.SSBlockTags;
import uk.joshiejack.simplyseasons.data.SSDatabase;
import uk.joshiejack.simplyseasons.data.SSLanguage;
import uk.joshiejack.simplyseasons.loot.SeasonCheck;
import uk.joshiejack.simplyseasons.world.season.AbstractSeasonsProvider;
import uk.joshiejack.simplyseasons.world.season.SeasonalCrops;
import uk.joshiejack.simplyseasons.world.season.SeasonsProvider;
import uk.joshiejack.simplyseasons.world.weather.AbstractWeatherProvider;
import uk.joshiejack.simplyseasons.world.weather.SeasonalWeatherProvider;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(SimplySeasons.MODID)
public class SimplySeasons {
    public static final String MODID = "simplyseasons";

    public SimplySeasons() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        SSSounds.SOUNDS.register(eventBus);
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, SSConfig.create());
        ctx.registerConfig(ModConfig.Type.CLIENT, SSClientConfig.create());
    }

    private void setup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ISeasonProvider.class, new AbstractSeasonsProvider.Storage(), () -> new SeasonsProvider(Season.MAIN));
        CapabilityManager.INSTANCE.register(IWeatherProvider.class, new AbstractWeatherProvider.Storage(), () -> new SeasonalWeatherProvider(Weather.CLEAR, 240000, 1));
    }

    @SubscribeEvent
    public static void registerLootData(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(MODID, "season"), SeasonCheck.SEASON);
    }

    @SubscribeEvent
    public static void onDataGathering(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new SSDatabase(generator));
            generator.addProvider(new SSBlockTags(generator, event.getExistingFileHelper()));
        }

        if (event.includeClient())
            generator.addProvider(new SSLanguage(generator));
    }

    public static class SSSounds {
        public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SimplySeasons.MODID);
        public static final RegistryObject<SoundEvent> SPRING = createSoundEvent("music.spring");
        public static final RegistryObject<SoundEvent> SUMMER = createSoundEvent("music.summer");
        public static final RegistryObject<SoundEvent> AUTUMN = createSoundEvent("music.autumn");
        public static final RegistryObject<SoundEvent> WINTER = createSoundEvent("music.winter");

        private static RegistryObject<SoundEvent> createSoundEvent(@Nonnull String name) {
            return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(SimplySeasons.MODID, name)));
        }
    }

    public static class SSConfig {
        public static ForgeConfigSpec.EnumValue<SeasonalCrops.CropOutOfSeasonEffect> cropOutOfSeasonEffect;

        SSConfig(ForgeConfigSpec.Builder builder) {
            cropOutOfSeasonEffect = builder.defineEnum("Crop out of season effect", SeasonalCrops.CropOutOfSeasonEffect.REPLACE_WITH_JUNK);
        }

        public static ForgeConfigSpec create() {
            return new ForgeConfigSpec.Builder().configure(SSConfig::new).getValue();
        }

    }
}