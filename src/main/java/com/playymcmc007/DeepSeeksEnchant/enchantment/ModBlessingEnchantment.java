package com.playymcmc007.DeepSeeksEnchant.enchantment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlessingEnchantment extends Enchantment {

    public ModBlessingEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
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
        if (otherId.equals(new ResourceLocation("deepseeksenchant", "advancements_blessing"))||
                otherId.equals(new ResourceLocation("deepseeksenchant", "time_blessing"))) {
            return false;
        }
        if (other instanceof DamageEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }
    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (!attacker.level().isClientSide()) {
            int modCount = ModList.get().getMods().size() - 3;// minecraft、forge、模组本体
            modCount = Math.max(modCount, 0);

            float damageMultiplier = 1.0f + (level - 1) * 0.1f;
            float bonusDamage = modCount * 0.1f * damageMultiplier;

            target.hurt(attacker.damageSources().mobAttack(attacker), bonusDamage);
        }
        super.doPostAttack(attacker, target, level);
    }
}