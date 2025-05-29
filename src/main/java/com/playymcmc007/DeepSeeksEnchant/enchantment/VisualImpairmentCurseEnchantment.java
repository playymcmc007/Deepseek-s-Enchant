package com.playymcmc007.DeepSeeksEnchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class VisualImpairmentCurseEnchantment extends Enchantment {
    public VisualImpairmentCurseEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public int getMinCost(int level) {
        return 30;
    }

    @Override
    public int getMaxCost(int level) {
        return 60;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isEffectActive() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.VISUAL_IMPAIRMENT.get(), helmet) > 0;
    }
}