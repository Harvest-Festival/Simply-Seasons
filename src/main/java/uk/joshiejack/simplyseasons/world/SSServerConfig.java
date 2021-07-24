package uk.joshiejack.simplyseasons.world;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class SSServerConfig {
    public static ForgeConfigSpec.BooleanValue useSSCropsHandler;
    public static ForgeConfigSpec.ConfigValue<List<String>> betterWeather2Dimensions;

    SSServerConfig(ForgeConfigSpec.Builder builder) {
        useSSCropsHandler = builder.comment("This should only be enabled if you have Serene Seasons installed. It will have Simply Seasons control the growth of crops instead of Serene Seasons. This will allow location based season features to work.").define("Use Simply Seasons crop handler", false);
        betterWeather2Dimensions = builder.comment("Due to limitations with the way Better Weather 2 syncs up their data (it occurs too late for me to determine which worlds have seasons), you will need to add dimensions that you wish seasons to be in to this list too.").define("BetterWeather2 Seasonal Dimensions", Lists.newArrayList("minecraft:overworld"));
    }

    public static ForgeConfigSpec create() {
        return new ForgeConfigSpec.Builder().configure(SSServerConfig::new).getValue();
    }
}