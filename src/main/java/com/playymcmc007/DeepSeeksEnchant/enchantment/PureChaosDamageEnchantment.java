package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.ChaosDamageConfig;
import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

public class PureChaosDamageEnchantment extends Enchantment {
    private static final Random RANDOM = new Random();

    private static final ResourceKey<DamageType>[] CURATED_VANILLA_TYPES = new ResourceKey[] {
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

    public PureChaosDamageEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 30 + (level - 1) * 15;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
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
        || other.equals(new ResourceLocation("deepseeksenchant", "chaos_damage"))
        || other.equals(new ResourceLocation("deepseeksenchant", "chaos_damage")
        )) {
            return false;
        }
        return super.checkCompatibility(other);
    }
    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int enchantLevel) {
        if (!EnchantmentToggleConfig.PURE_ENABLED.get()) {
            return;
        }
        if (!(target instanceof LivingEntity livingTarget)) return;
        if (attacker.level().isClientSide) return;

        ResourceKey<DamageType> damageType = (enchantLevel == 1)
                ? getRandomVanillaType()
                : getRandomGlobalType(attacker.level().registryAccess());

        float damageAmount = getAttackDamage(attacker);
        livingTarget.hurt(createDamageSource(attacker.level(), attacker, damageType), damageAmount);

        if (attacker instanceof ServerPlayer player && ChaosDamageConfig.shouldShowDamageMessages()) {
            sendDamageMessage(player, damageType, damageAmount);
        }
    }
    private float getAttackDamage(LivingEntity entity) {
        return (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    private void sendDamageMessage(ServerPlayer player, ResourceKey<DamageType> type, float damage) {
        ResourceLocation typeId = type.location();
        String namespace = typeId.getNamespace();
        String path = typeId.getPath();

        String header = Component.translatable("message.deepseeksenchant.pure_chaos.header").getString();
        String typePrefix = namespace.equals("minecraft")
                ? Component.translatable("message.deepseeksenchant.source.vanilla").getString()
                : Component.translatable("message.deepseeksenchant.source.mod", namespace).getString();
        String typeLine = Component.translatable("message.deepseeksenchant.pure_chaos.type",
                typePrefix, path.toUpperCase()).getString();
        String damageLine = Component.translatable("message.deepseeksenchant.pure_chaos.damage",
                String.format("%.1f", damage)).getString();

        String message = String.format("%s\n%s\n%s",
                header, typeLine, damageLine);

        player.sendSystemMessage(Component.literal(message));
    }

    private ResourceKey<DamageType> getRandomVanillaType() {
        return CURATED_VANILLA_TYPES[RANDOM.nextInt(CURATED_VANILLA_TYPES.length)];
    }

    private ResourceKey<DamageType> getRandomGlobalType(RegistryAccess access) {
        List<ResourceKey<DamageType>> allTypes = access.registryOrThrow(Registries.DAMAGE_TYPE)
                .registryKeySet()
                .stream()
                .toList();
        return allTypes.get(RANDOM.nextInt(allTypes.size()));
    }

    private DamageSource createDamageSource(Level level, LivingEntity attacker, ResourceKey<DamageType> type) {
        return new DamageSource(
                level.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(type),
                attacker
        );
    }
}