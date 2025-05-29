package com.playymcmc007.DeepSeeksEnchant.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ChaosDamageConfig {
    private static ForgeConfigSpec.BooleanValue showDamageMessages;
    private static ForgeConfigSpec configSpec;

    public static void init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Chaos Damage Enchantment Settings")
                .push("general");

        showDamageMessages = builder
                .comment("Whether to show damage type messages")
                .define("showDamageMessages", false);

        builder.pop();
        configSpec = builder.build();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);
    }

    public static boolean shouldShowDamageMessages() {
        return showDamageMessages.get();
    }

    public static void setShowDamageMessages(boolean show) {
        showDamageMessages.set(show);
        showDamageMessages.save();
    }
}