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
    public static ForgeConfigSpec.IntValue dryFogDensityMultiplier;
    public static ForgeConfigSpec.BooleanValue enableBlizzardNoise;
    public static ForgeConfigSpec.BooleanValue enableBlizzardTexture;
    public static ForgeConfigSpec.BooleanValue seasonalMusic;
    public static ForgeConfigSpec.BooleanValue seasonInDebug;
    public static ForgeConfigSpec.BooleanValue requireItemInInventoryForHUD;
    public static ForgeConfigSpec.EnumValue<DryFog> dryFogSetting;

    SSClientConfig(ForgeConfigSpec.Builder builder) {
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

    public static ForgeConfigSpec create() {
        return new ForgeConfigSpec.Builder().configure(SSClientConfig::new).getValue();
    }

    public enum DryFog {
        OFF, STANDARD, SANDY
    }
}