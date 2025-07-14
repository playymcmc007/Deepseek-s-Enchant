package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LandSwimmerEnchantment extends Enchantment {

    public LandSwimmerEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMinCost(int level) {
        return 30;
    }

    @Override
    public int getMaxCost(int level) {
        return 40;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentToggleConfig.LAND_SWIMMER_ENABLED.get()) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(ModEnchantments.LAND_SWIMMER.get()) > 0) {

            player.setPose(Pose.SWIMMING);
            player.setSwimming(true);
        }
    }
    @SubscribeEvent
    // 生物
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!EnchantmentToggleConfig.LAND_SWIMMER_ENABLED.get()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;
        if (entity.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(ModEnchantments.LAND_SWIMMER.get()) > 0) {
            entity.setPose(Pose.SWIMMING);
            entity.setSwimming(true);
        }
    }
}