package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.ChaosDamageConfig;
import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChaosDamageEnchantment extends Enchantment {
    private static final Random RANDOM = new Random();

    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 2;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 2;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other.equals(new ResourceLocation("deepseeksenchant", "snipe"))
        || other.equals(new ResourceLocation("deepseeksenchant", "pure_chaos_damage"))) {
            return false;
        }
        return super.checkCompatibility(other);
    }

    private static List<ResourceKey<DamageType>> getAllDamageTypes(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(Registries.DAMAGE_TYPE)
                .registryKeySet()
                .stream()
                .toList();
    }

    private static final ResourceKey<DamageType>[] DAMAGE_TYPE_KEYS = new ResourceKey[] {
            DamageTypes.ARROW,
            DamageTypes.BAD_RESPAWN_POINT,
            DamageTypes.CACTUS,
            DamageTypes.CRAMMING,
            DamageTypes.DROWN,
            DamageTypes.DRAGON_BREATH,
            DamageTypes.DRY_OUT,
            DamageTypes.EXPLOSION,
            DamageTypes.FALL,
            DamageTypes.FALLING_ANVIL,
            DamageTypes.FALLING_BLOCK,
            DamageTypes.FALLING_STALACTITE,
            DamageTypes.FIREBALL,
            DamageTypes.FIREWORKS,
            DamageTypes.FREEZE,
            DamageTypes.FLY_INTO_WALL,
            DamageTypes.FELL_OUT_OF_WORLD,
            DamageTypes.GENERIC,
            DamageTypes.GENERIC_KILL,
            DamageTypes.HOT_FLOOR,
            DamageTypes.IN_FIRE,
            DamageTypes.IN_WALL,
            DamageTypes.INDIRECT_MAGIC,
            DamageTypes.LAVA,
            DamageTypes.LIGHTNING_BOLT,
            DamageTypes.MAGIC,
            DamageTypes.MOB_ATTACK,
            DamageTypes.MOB_ATTACK_NO_AGGRO,
            DamageTypes.MOB_PROJECTILE,
            DamageTypes.ON_FIRE,
            DamageTypes.OUTSIDE_BORDER,
            DamageTypes.PLAYER_ATTACK,
            DamageTypes.PLAYER_EXPLOSION,
            DamageTypes.SONIC_BOOM,
            DamageTypes.STALAGMITE,
            DamageTypes.STARVE,
            DamageTypes.STING,
            DamageTypes.SWEET_BERRY_BUSH,
            DamageTypes.THORNS,
            DamageTypes.TRIDENT,
            DamageTypes.THROWN,
            DamageTypes.UNATTRIBUTED_FIREBALL,
            DamageTypes.WITHER,
            DamageTypes.WITHER_SKULL
    };

    public ChaosDamageEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    private float getBaseDamage(LivingEntity attacker, LivingEntity target, int level) {

        return (float)attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (!EnchantmentToggleConfig.CHAOS_DAMAGE_ENABLED.get()) {
            return;
        }
        if (!(attacker instanceof ServerPlayer attackingPlayer)) {
            return;
        }

        if (!(target instanceof LivingEntity livingTarget)) return;

        ItemStack weapon = attacker.getMainHandItem();
        if (!weapon.isEnchanted() ||
                EnchantmentHelper.getItemEnchantmentLevel(this, weapon) <= 0) {
            return;
        }
        java.util.Map<ResourceKey<DamageType>, Integer> damageTypeCounts = new java.util.HashMap<>();
        float damageAmount = getBaseDamage(attacker, livingTarget, level);
        int points = (int) damageAmount;

        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(this, weapon);
        for (int i = 0; i < points; i++) {
            ResourceKey<DamageType> randomDamageType = enchantLevel == 1
                    ? getVanillaRandomDamageType()  // Ⅰ级用原版
                    : getAllRandomDamageType(attacker.level().registryAccess()); // Ⅱ级用全部
            System.out.println(createDamageSource(attacker.level(), attacker, randomDamageType));
            target.hurt(createDamageSource(attacker.level(), attacker, randomDamageType), 1.0F);
            damageTypeCounts.merge(randomDamageType, 1, Integer::sum);
        }
        if (ChaosDamageConfig.shouldShowDamageMessages()) {
            StringBuilder msg = new StringBuilder(
                    Component.translatable("message.deepseeksenchant.chaos_damage.header")
                            .withStyle(ChatFormatting.GOLD).getString() + "\n");

            Map<String, Integer> sourceCounts = new HashMap<>();

            damageTypeCounts.forEach((type, count) -> {
                ResourceLocation id = type.location();
                String source = id.getNamespace();
                String typeName = id.getPath();

                String prefix = Component.translatable(
                        source.equals("minecraft") ?
                                "message.deepseeksenchant.source.vanilla" :
                                "message.deepseeksenchant.source.mod",
                        source.equals("minecraft") ? "" : source
                ).getString();

                msg.append(Component.translatable(
                        "message.deepseeksenchant.chaos_damage.entry",
                        prefix,
                        typeName,
                        count
                ).getString()).append("\n");

                sourceCounts.merge(source, 1, Integer::sum);
            });

            StringBuilder sourceStats = new StringBuilder(
                    Component.translatable("message.deepseeksenchant.chaos_damage.sources_header")
                            .withStyle(ChatFormatting.GREEN).getString());

            sourceCounts.forEach((source, count) -> {
                String color = source.equals("minecraft") ? "§b" : "§d";
                sourceStats.append(color)
                        .append(source)
                        .append("×")
                        .append(count)
                        .append(" §7| ");
            });

            if (!sourceCounts.isEmpty()) {
                sourceStats.setLength(sourceStats.length() - 3);
            }

            sourceStats.append(
                    Component.translatable(
                            "message.deepseeksenchant.chaos_damage.total_types",
                            damageTypeCounts.size()
                    ).getString());

            attackingPlayer.sendSystemMessage(Component.literal(msg.toString() + sourceStats));
        }
    }

    private ResourceKey<DamageType> getVanillaRandomDamageType() {
        return DAMAGE_TYPE_KEYS[RANDOM.nextInt(DAMAGE_TYPE_KEYS.length)];
    }

    private ResourceKey<DamageType> getAllRandomDamageType(RegistryAccess registryAccess) {
        List<ResourceKey<DamageType>> damageTypes = getAllDamageTypes(registryAccess);
        return damageTypes.get(RANDOM.nextInt(damageTypes.size()));
    }

    private DamageSource createDamageSource(Level level, LivingEntity attacker, ResourceKey<DamageType> damageTypeKey) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageTypeKey), attacker);
    }

}