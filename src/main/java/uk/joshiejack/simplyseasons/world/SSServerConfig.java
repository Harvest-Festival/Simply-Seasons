package uk.joshiejack.simplyseasons.world;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SSServerConfig {
    public static ModConfigSpec.BooleanValue useSSCropsHandler;

    SSServerConfig(ModConfigSpec.Builder builder) {
        useSSCropsHandler = builder.comment("This should only be enabled if you have Serene Seasons installed. It will have Simply Seasons control the growth of crops instead of Serene Seasons. This will allow location based season features to work.").define("Use Simply Seasons crop handler", false);
    }

    public static ModConfigSpec create() {
        return new ModConfigSpec.Builder().configure(SSServerConfig::new).getValue();
    }
}