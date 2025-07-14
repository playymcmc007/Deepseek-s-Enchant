package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class DimensionalLockEnchantment extends Enchantment {

    public DimensionalLockEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 30;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 60;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Mod.EventBusSubscriber
    public static class DimensionalLockHandler {
        @SubscribeEvent
        public static void onTravelToDimension(EntityTravelToDimensionEvent event) {
            if (!EnchantmentToggleConfig.DIMENSIONAL_LOCK_ENABLED.get()) {
                return;
            }
            if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player &&
                    player.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(ModEnchantments.DIMENSIONAL_LOCK.get()) > 0) {
                event.setCanceled(true);
                player.displayClientMessage(
                        Component.translatable("message.deepseeksenchant.dim.tip"),
                        true
                );
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
}