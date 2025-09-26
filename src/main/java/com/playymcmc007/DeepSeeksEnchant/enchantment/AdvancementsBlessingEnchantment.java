package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

public class AdvancementsBlessingEnchantment extends Enchantment {
    public AdvancementsBlessingEnchantment() {
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
        if (otherId.equals(new ResourceLocation("deepseeksenchant", "mod_blessing"))||
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
        if (!EnchantmentToggleConfig.ADVBLESSING_ENABLED.get()) {
            super.doPostAttack(attacker, target, level);
            return;
        }
        if (!attacker.level().isClientSide() && attacker instanceof ServerPlayer player) {
            int completedAdvancements = getCompletedAdvancementsCount(player);
            float damageMultiplier = 1.0f + (level - 1 ) * 0.1f;
            float bonusDamage = (completedAdvancements / 10.0f) * damageMultiplier;
            target.hurt(player.damageSources().mobAttack(player), bonusDamage);
        }
        super.doPostAttack(attacker, target, level);
    }

    private int getCompletedAdvancementsCount(ServerPlayer player) {
        int count = 0;
        Collection<Advancement> allAdvancements = player.server.getAdvancements().getAllAdvancements();
        for (Advancement advancement : allAdvancements) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (progress.isDone()) {
                count++;
            }
        }
        return count;
    }
}