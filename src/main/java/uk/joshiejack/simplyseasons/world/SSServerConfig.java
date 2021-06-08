package uk.joshiejack.simplyseasons.world;

import net.minecraftforge.common.ForgeConfigSpec;

public class SSServerConfig {
    public static ForgeConfigSpec.BooleanValue useSSCropsHandler;

    SSServerConfig(ForgeConfigSpec.Builder builder) {
        useSSCropsHandler = builder.comment("This should only be enabled if you have Serene Seasons installed. It will have Simply Seasons control the growth of crops instead of Serene Seasons. This will allow location based season features to work.").define("Use Simply Seasons crop handler", false);
    }

    public static ForgeConfigSpec create() {
        return new ForgeConfigSpec.Builder().configure(SSServerConfig::new).getValue();
    }
}
