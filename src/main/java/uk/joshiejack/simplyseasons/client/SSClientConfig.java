package uk.joshiejack.simplyseasons.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class SSClientConfig {
    public static ForgeConfigSpec.BooleanValue enableHUD;
    public static ForgeConfigSpec.BooleanValue enableCropsTooltip;
    public static ForgeConfigSpec.BooleanValue showWetDryTooltip;
    public static ForgeConfigSpec.IntValue overallFogDensity;
    public static ForgeConfigSpec.IntValue snowDensityMultiplier;
    public static ForgeConfigSpec.IntValue blizzardDensityMultiplier;
    public static ForgeConfigSpec.IntValue fogDensityMultiplier;
    public static ForgeConfigSpec.BooleanValue enableBlizzardNoise;
    public static ForgeConfigSpec.BooleanValue seasonalMusic;
    public static ForgeConfigSpec.BooleanValue seasonInDebug;
    public static ForgeConfigSpec.BooleanValue requireItemInInventoryForHUD;

    SSClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Information");
        enableHUD = builder.define("Enable season HUD", false);
        enableCropsTooltip = builder.define("Enable crops season tooltip", true);
        showWetDryTooltip = builder.define("Show wet/dry tooltip", false);
        seasonalMusic = builder.define("Enable seasonal music", false);
        seasonInDebug = builder.define("Display season in debug menu", true);
        requireItemInInventoryForHUD = builder.define("Require calendar items in inventory to display the HUD", false);
        builder.pop();
        builder.push("Weather");
        enableBlizzardNoise = builder.define("Enable blizzard sound effect", true);
        overallFogDensity = builder.comment("Set this to 0 to disable all types of fogs").defineInRange("Fog/Blizzard density", 20, 0, 100);
        snowDensityMultiplier = builder.defineInRange("Fog density multiplier (Snow)", 5, 0, 100);
        blizzardDensityMultiplier = builder.defineInRange("Fog density multiplier (Blizzard)", 25, 0, 100);
        fogDensityMultiplier = builder.defineInRange("Fog density multiplier (Fog)", 10, 0, 100);
        builder.pop();
    }

    public static ForgeConfigSpec create() {
        return new ForgeConfigSpec.Builder().configure(SSClientConfig::new).getValue();
    }
}