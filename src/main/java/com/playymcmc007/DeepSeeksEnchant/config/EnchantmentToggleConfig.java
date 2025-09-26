package com.playymcmc007.DeepSeeksEnchant.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentToggleConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue SPLIT_ENABLED;
    public static ForgeConfigSpec.BooleanValue SNIPE_ENABLED;
    public static ForgeConfigSpec.BooleanValue CHAOS_DAMAGE_ENABLED;
    public static ForgeConfigSpec.BooleanValue PURE_ENABLED;
    public static ForgeConfigSpec.BooleanValue DDR_ENABLED;
    public static ForgeConfigSpec.BooleanValue BERSERK_CHOP_ENABLED;
    public static ForgeConfigSpec.BooleanValue OMNIGUARD_ENABLED;
    public static ForgeConfigSpec.BooleanValue ADVBLESSING_ENABLED;
    public static ForgeConfigSpec.BooleanValue MODBLESSING_ENABLED;
    public static ForgeConfigSpec.BooleanValue TIMEBLESSING_ENABLED;
    public static ForgeConfigSpec.BooleanValue GRENADIER_ENABLED;
    public static ForgeConfigSpec.BooleanValue BLOOD_FEEDING_ENABLED;
    public static ForgeConfigSpec.BooleanValue IRON_HEAD_ENABLED;
    public static ForgeConfigSpec.BooleanValue ILLITERACY_ENABLED;
    public static ForgeConfigSpec.BooleanValue VISUAL_IMPAIRMENT_ENABLED;
    public static ForgeConfigSpec.BooleanValue STRANGE_NOISES_ENABLED;
    public static ForgeConfigSpec.BooleanValue ANTI_GLYPH_ENABLED;
    public static ForgeConfigSpec.BooleanValue ROBBERY_ENABLED;
    public static ForgeConfigSpec.BooleanValue GIANT_GROWTH_ENABLED;
    public static ForgeConfigSpec.BooleanValue REGURGITATION_ENABLED;
    public static ForgeConfigSpec.BooleanValue DIMENSIONAL_LOCK_ENABLED;
    public static ForgeConfigSpec.BooleanValue DEEPDARKSCALL_ENABLED;
    public static ForgeConfigSpec.BooleanValue LAND_SWIMMER_ENABLED;
    public static ForgeConfigSpec.BooleanValue ELDER_GUARDIAN_DISTURBANCE_ENABLED;
    public static ForgeConfigSpec.BooleanValue DISGUISE_AND_DECEPTION_ENABLED;
    public static ForgeConfigSpec.BooleanValue FRAGILE_ENABLED;

    static {
        BUILDER.translation("config.deepseeksenchant.enchantments").push("enchantments");

        SPLIT_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.split_enabled")
                .define("split_enabled", true);

        SNIPE_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.snipe_enabled")
                .define("snipe_enabled", true);

        CHAOS_DAMAGE_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.chaos_damage_enabled")
                .define("chaos_damage_enabled", true);

        PURE_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.pure_enabled")
                .define("pure_enabled", true);

        DDR_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.dance_dance_revolution_enabled")
                .define("dance_dance_revolution_enabled", true);

        BERSERK_CHOP_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.berserk_chop_enabled")
                .define("berserk_chop_enabled", true);

        OMNIGUARD_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.omniguard_enabled")
                .define("omniguard_enabled", true);

        ADVBLESSING_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.advancements_blessing_enabled")
                .define("advancements_blessing_enabled", true);

        MODBLESSING_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.mod_blessing_enabled")
                .define("mod_blessing_enabled", true);

        TIMEBLESSING_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.time_blessing_enabled")
                .define("time_blessing_enabled", true);

        GRENADIER_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.grenadier_enabled")
                .define("grenadier_enabled", true);

        BLOOD_FEEDING_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.blood_feeding_enabled")
                .define("blood_feeding_enabled", true);

        IRON_HEAD_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.iron_head_enabled")
                .define("iron_head_enabled", true);

        ILLITERACY_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.illiteracy_enabled")
                .define("illiteracy_enabled", true);

        VISUAL_IMPAIRMENT_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.visual_impairment_enabled")
                .define("visual_impairment_enabled", true);

        STRANGE_NOISES_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.strange_noises_enabled")
                .define("strange_noises_enabled", true);

        ANTI_GLYPH_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.anti_glyph_enabled")
                .define("anti_glyph_enabled", true);

        ROBBERY_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.robbery_enabled")
                .define("robbery_enabled", true);

        GIANT_GROWTH_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.giant_growth_enabled")
                .define("giant_growth_enabled", true);

        REGURGITATION_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.regurgitation_enabled")
                .define("regurgitation_enabled", true);

        DIMENSIONAL_LOCK_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.dimensional_lock_enabled")
                .define("dimensional_lock_enabled", true);

        DEEPDARKSCALL_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.deepdarkscall_enabled")
                .define("deepdarkscall_enabled", true);

        LAND_SWIMMER_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.land_swimmer_enabled")
                .define("land_swimmer_enabled", true);

        ELDER_GUARDIAN_DISTURBANCE_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.elder_guardian_disturbance_enabled")
                .define("elder_guardian_disturbance_enabled", true);

        DISGUISE_AND_DECEPTION_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.disguise_and_deception_enabled")
                .define("disguise_and_deception_enabled", false);

        FRAGILE_ENABLED = BUILDER.translation("config.deepseeksenchant.enchantments.fragile_enabled")
                .define("fragile_enabled", false);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "deepseeksenchant/enchantments.toml");
    }

    public static boolean isEnabled(RegistryObject<Enchantment> enchantment) {
        String path = enchantment.getId().getPath();
        return switch (path) {
            case "split" -> SPLIT_ENABLED.get();
            case "snipe" -> SNIPE_ENABLED.get();
            case "chaos_damage" -> CHAOS_DAMAGE_ENABLED.get();
            case "pure_chaos_damage" -> PURE_ENABLED.get();
            case "dance_dance_revolution" -> DDR_ENABLED.get();
            case "berserk_chop" -> BERSERK_CHOP_ENABLED.get();
            case "omniguard" -> OMNIGUARD_ENABLED.get();
            case "advancements_blessing" -> ADVBLESSING_ENABLED.get();
            case "mod_blessing" -> MODBLESSING_ENABLED.get();
            case "time_blessing" -> TIMEBLESSING_ENABLED.get();
            case "grenadier" -> GRENADIER_ENABLED.get();
            case "blood_feeding" -> BLOOD_FEEDING_ENABLED.get();
            case "iron_head" -> IRON_HEAD_ENABLED.get();
            case "illiteracy_curse" -> ILLITERACY_ENABLED.get();
            case "visual_impairment_curse" -> VISUAL_IMPAIRMENT_ENABLED.get();
            case "strange_noises_curse" -> STRANGE_NOISES_ENABLED.get();
            case "anti_glyph_curse" -> ANTI_GLYPH_ENABLED.get();
            case "robbery" -> ROBBERY_ENABLED.get();
            case "giant_growth" -> GIANT_GROWTH_ENABLED.get();
            case "regurgitation" -> REGURGITATION_ENABLED.get();
            case "dimensional_lock" -> DIMENSIONAL_LOCK_ENABLED.get();
            case "deepdarkscall" -> DEEPDARKSCALL_ENABLED.get();
            case "land_swimmer" -> LAND_SWIMMER_ENABLED.get();
            case "elder_guardian_disturbance" -> ELDER_GUARDIAN_DISTURBANCE_ENABLED.get();
            case "disguise_and_deception_curse" -> DISGUISE_AND_DECEPTION_ENABLED.get();
            case "fragile" -> FRAGILE_ENABLED.get();
            default -> true;
        };
    }
}