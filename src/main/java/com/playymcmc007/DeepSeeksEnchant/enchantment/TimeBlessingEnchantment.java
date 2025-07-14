package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.server.level.ServerLevel;

public class TimeBlessingEnchantment extends Enchantment {

    public TimeBlessingEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 20 + 5 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 10;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        ResourceLocation otherId = ForgeRegistries.ENCHANTMENTS.getKey(other);
        if (otherId.equals(new ResourceLocation("deepseeksenchant", "advancements_blessing")) ||
                otherId.equals(new ResourceLocation("deepseeksenchant", "mod_blessing"))) {
            return false;
        }
        if (other instanceof DamageEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (!EnchantmentToggleConfig.TIMEBLESSING_ENABLED.get()) {
            super.doPostAttack(attacker, target, level);
            return;
        }
        if (!attacker.level().isClientSide() && attacker.level() instanceof ServerLevel world) {
            long dayTime = world.getDayTime();
            long daysPassed = dayTime / 24000L;
            float damageMultiplier = 1.0f + (level - 1) * 0.1f;
            float bonusDamage = daysPassed * 0.1f * damageMultiplier;


            target.hurt(attacker.damageSources().mobAttack(attacker), bonusDamage);
        }
        super.doPostAttack(attacker, target, level);
    }
}