package uk.joshiejack.simplyseasons;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uk.joshiejack.simplyseasons.api.ISeasonsProvider;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.data.SSDatabase;
import uk.joshiejack.simplyseasons.data.SSLanguage;
import uk.joshiejack.simplyseasons.loot.SeasonCheck;
import uk.joshiejack.simplyseasons.world.SeasonsProvider;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(SimplySeasons.MODID)
public class SimplySeasons {
    public static final String MODID = "simplyseasons";
    public static final int DAYS_PER_SEASON = 28;

    public SimplySeasons() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ISeasonsProvider.class, new SeasonsProvider.Storage(), () -> new SeasonsProvider(Season.MAIN));
    }

    @SubscribeEvent
    public static void registerLootData(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(MODID, "season"), SeasonCheck.SEASON);
    }

    @SubscribeEvent
    public static void onDataGathering(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        if (event.includeServer())
            generator.addProvider(new SSDatabase(generator));
        if (event.includeClient())
            generator.addProvider(new SSLanguage(generator));
    }
}
