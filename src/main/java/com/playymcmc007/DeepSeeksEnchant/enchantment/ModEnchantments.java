package com.playymcmc007.DeepSeeksEnchant.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "deepseeksenchant");

    public static final RegistryObject<Enchantment> SPLIT = ENCHANTMENTS.register("split",
            SplitEnchantment::new);
    public static final RegistryObject<Enchantment> SNIPE = ENCHANTMENTS.register("snipe",
            SnipeEnchantment::new);
    public static final RegistryObject<Enchantment> CHAOS_DAMAGE = ENCHANTMENTS.register("chaos_damage",
            ChaosDamageEnchantment::new);
    public static final RegistryObject<Enchantment> PURE = ENCHANTMENTS.register("pure_chaos_damage",
            PureChaosDamageEnchantment::new);
    public static final RegistryObject<Enchantment> DDR = ENCHANTMENTS.register("dance_dance_revolution",
            Dance_Dance_RevolutionEnchantment::new);
    public static final RegistryObject<Enchantment> BERSERK_CHOP = ENCHANTMENTS.register("berserk_chop",
            Berserk_ChopEnchantment::new);
    public static final RegistryObject<Enchantment> OMNIGUARD = ENCHANTMENTS.register("omniguard",
            OmniGuardEnchantment::new);
    public static final RegistryObject<Enchantment> ADVBLESSING = ENCHANTMENTS.register("advancements_blessing",
            AdvancementsBlessingEnchantment::new);
    public static final RegistryObject<Enchantment> MODBLESSING = ENCHANTMENTS.register("mod_blessing",
            ModBlessingEnchantment::new);
    public static final RegistryObject<Enchantment> TIMEBLESSING = ENCHANTMENTS.register("time_blessing",
            TimeBlessingEnchantment::new);
    public static final RegistryObject<Enchantment> GRENADIER = ENCHANTMENTS.register("grenadier",
            GrenadierEnchantment::new);
    public static final RegistryObject<Enchantment> BLOOD_FEEDING = ENCHANTMENTS.register("blood_feeding",
            BloodFeedingEnchantment::new);
    public static final RegistryObject<Enchantment> IRON_HEAD = ENCHANTMENTS.register("iron_head",
            IronHeadEnchantment::new);
    public static final RegistryObject<Enchantment> ILLITERACY = ENCHANTMENTS.register("illiteracy_curse",
            IlliteracyCurseEnchantment::new);
    public static final RegistryObject<Enchantment> VISUAL_IMPAIRMENT = ENCHANTMENTS.register("visual_impairment_curse",
            VisualImpairmentCurseEnchantment::new);
    public static final RegistryObject<Enchantment> STRANGE_NOISES_CURSE = ENCHANTMENTS.register("strange_noises_curse",
            StrangeNoisesCurseEnchantment::new);
    public static final RegistryObject<Enchantment> ANTI_GLYPH = ENCHANTMENTS.register("anti_glyph_curse",
            AntiGlyphCurseEnchantment::new);

    public static final RegistryObject<Enchantment> ROBBERY = ENCHANTMENTS.register("robbery",
            RobberyEnchantment::new);
    public static final RegistryObject<Enchantment> GIANT_GROWTH = ENCHANTMENTS.register("giant_growth",
            GiantGrowthEnchantment::new);
    public static final RegistryObject<Enchantment> REGURGITATION = ENCHANTMENTS.register("regurgitation",
            RegurgitationEnchantment::new);
    public static final RegistryObject<Enchantment> DIMENSIONAL_LOCK = ENCHANTMENTS.register("dimensional_lock",
            DimensionalLockEnchantment::new);
    public static final RegistryObject<Enchantment> DEEPDARKSCALL = ENCHANTMENTS.register("deepdarkscall",
            DeepdarksCallEnchantment::new);
    public static final RegistryObject<Enchantment> LAND_SWIMMER = ENCHANTMENTS.register("land_swimmer",
            LandSwimmerEnchantment::new);
    public static final RegistryObject<Enchantment> ELDER_GUARDIAN_DISTURBANCE = ENCHANTMENTS.register("elder_guardian_disturbance",
            ElderGuardianDisturbanceEnchantment::new);
    public static final RegistryObject<Enchantment> DISGUISE_AND_DECEPTION = ENCHANTMENTS.register("disguise_and_deception_curse",
            DisguiseAndDeceptionCurseEnchantment::new);
    public static final RegistryObject<Enchantment> FRAGILE = ENCHANTMENTS.register("fragile",
            FragileEnchantment::new);
    public static final RegistryObject<Enchantment> BROKEN_ARROW = ENCHANTMENTS.register("broken_arrow",
            BrokenArrowEnchantment::new);
    public static final RegistryObject<Enchantment> LURE_MOB = ENCHANTMENTS.register("lure_mob",
            LureMobEnchantment::new);
}
