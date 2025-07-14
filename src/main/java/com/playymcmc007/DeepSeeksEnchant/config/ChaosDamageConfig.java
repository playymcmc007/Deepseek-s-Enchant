package com.playymcmc007.DeepSeeksEnchant.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ChaosDamageConfig {
    private static ForgeConfigSpec.BooleanValue showDamageMessages;
    private static ForgeConfigSpec configSpec;

    public static void init() {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        BUILDER.translation("config.deepseeksenchant.chaos_damage")
                .push("chaos_damage");
        showDamageMessages = BUILDER.translation("config.deepseeksenchant.chaos_damage.showDamageMessages")
                .comment(("The damage type dealt is displayed in the chat box."))
                .define("showDamageMessages", true);


        BUILDER.pop();
        configSpec = BUILDER.build();

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