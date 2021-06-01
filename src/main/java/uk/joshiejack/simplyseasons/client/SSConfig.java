package uk.joshiejack.simplyseasons.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class SSConfig {
    public static ForgeConfigSpec.BooleanValue enableHUD;
    public static ForgeConfigSpec.BooleanValue enableCropsTooltip;

    SSConfig(ForgeConfigSpec.Builder builder) {
        enableHUD = builder.define("Enable Season HUD", true);
        enableCropsTooltip = builder.define("Enable Crops Season Tooltip", true);
    }

    public static ForgeConfigSpec create() {
        return new ForgeConfigSpec.Builder().configure(SSConfig::new).getValue();
    }
}
