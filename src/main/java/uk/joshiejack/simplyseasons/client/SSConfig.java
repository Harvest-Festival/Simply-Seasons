package uk.joshiejack.simplyseasons.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

@OnlyIn(Dist.CLIENT)
public class SSConfig {
    public static ForgeConfigSpec.BooleanValue enableHUD;
    public static ForgeConfigSpec.BooleanValue enableCropsTooltip;
    public static ForgeConfigSpec.IntValue fogDensity;
    public static ForgeConfigSpec.BooleanValue seasonalMusic;

    SSConfig(ForgeConfigSpec.Builder builder) {
        builder.push("client");
        enableHUD = builder.define("Enable season HUD", true);
        enableCropsTooltip = builder.define("Enable crops season tooltip", true);
        fogDensity = builder.defineInRange("Fog/Blizzard density", 30, 0, 100);
        seasonalMusic = builder.define("Enable seasonal music", false);
        builder.pop();
    }

    public static ForgeConfigSpec create() {
        return new ForgeConfigSpec.Builder().configure(SSConfig::new).getValue();
    }
}
