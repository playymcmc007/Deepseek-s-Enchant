package com.playymcmc007.DeepSeeksEnchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BrokenArrowEnchantment extends Enchantment {

    public BrokenArrowEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 5;
    }

    @Override
    public int getMaxCost(int level) {
        return 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEnchant(net.minecraft.world.item.ItemStack stack) {
        return stack.getItem() instanceof net.minecraft.world.item.BowItem ||
                stack.getItem() instanceof net.minecraft.world.item.CrossbowItem;
    }
}