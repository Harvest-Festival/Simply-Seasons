package uk.joshiejack.simplyseasons.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class SSClientConfig {
    public static ForgeConfigSpec.BooleanValue enableHUD;
    public static ForgeConfigSpec.BooleanValue enableCropsTooltip;
    public static ForgeConfigSpec.BooleanValue showWetDryTooltip;
    public static ForgeConfigSpec.IntValue fogDensity;
    public static ForgeConfigSpec.BooleanValue seasonalMusic;

    SSClientConfig(ForgeConfigSpec.Builder builder) {
        enableHUD = builder.define("Enable season HUD", true);
        enableCropsTooltip = builder.define("Enable crops season tooltip", true);
        showWetDryTooltip = builder.define("Show wet/dry tooltip", false);
        fogDensity = builder.defineInRange("Fog/Blizzard density", 30, 0, 100);
        seasonalMusic = builder.define("Enable seasonal music", false);
    }

    public static ForgeConfigSpec create() {
        return new ForgeConfigSpec.Builder().configure(SSClientConfig::new).getValue();
    }
}