package uk.joshiejack.simplyseasons.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SSClientConfig {
    public static ModConfigSpec.BooleanValue enableHUD;
    public static ModConfigSpec.BooleanValue enableCropsTooltip;
    public static ModConfigSpec.BooleanValue showWetDryTooltip;
    public static ModConfigSpec.IntValue overallFogDensity;
    public static ModConfigSpec.IntValue snowDensityMultiplier;
    public static ModConfigSpec.IntValue blizzardDensityMultiplier;
    public static ModConfigSpec.IntValue fogDensityMultiplier;
    public static ModConfigSpec.IntValue dryFogDensityMultiplier;
    public static ModConfigSpec.BooleanValue enableBlizzardNoise;
    public static ModConfigSpec.BooleanValue enableBlizzardTexture;
    public static ModConfigSpec.BooleanValue seasonalMusic;
    public static ModConfigSpec.BooleanValue seasonInDebug;
    public static ModConfigSpec.BooleanValue requireItemInInventoryForHUD;
    public static ModConfigSpec.EnumValue<DryFog> dryFogSetting;

    SSClientConfig(ModConfigSpec.Builder builder) {
        builder.push("Information");
        enableHUD = builder.define("Enable season HUD", false);
        enableCropsTooltip = builder.define("Enable crops season tooltip", true);
        showWetDryTooltip = builder.define("Show wet/dry tooltip", false);
        seasonalMusic = builder.define("Enable seasonal music", false);
        seasonInDebug = builder.define("Display season in debug menu", true);
        requireItemInInventoryForHUD = builder.comment("This config option requires Enable season HUD to be true. If it is false it will not work.").define("Require calendar items in inventory to display the HUD", false);
        builder.pop();
        builder.push("Weather");
        enableBlizzardNoise = builder.define("Enable blizzard sound effect", true);
        enableBlizzardTexture = builder.define("Enable blizzard texture", true);
        overallFogDensity = builder.comment("Set this to 0 to disable all types of fogs").defineInRange("Fog/Blizzard density", 20, 0, 100);
        snowDensityMultiplier = builder.defineInRange("Fog density multiplier (Snow)", 5, 0, 100);
        blizzardDensityMultiplier = builder.defineInRange("Fog density multiplier (Blizzard)", 25, 0, 100);
        fogDensityMultiplier = builder.defineInRange("Fog density multiplier (Fog)", 10, 0, 100);
        dryFogDensityMultiplier = builder.defineInRange("Fog density multiplier (Dry Fog)", 15, 0, 100);
        dryFogSetting = builder.defineEnum("Dry biomes fog style", DryFog.SANDY);
        builder.pop();
    }

    public static ModConfigSpec create() {
        return new ModConfigSpec.Builder().configure(SSClientConfig::new).getValue();
    }

    public enum DryFog {
        OFF, STANDARD, SANDY
    }
}