package com.playymcmc007.DeepSeeksEnchant.server;

import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import com.playymcmc007.DeepSeeksEnchant.events.RegurgitationHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ServerEvents {
    public static void handleRegurgitation(ServerPlayer player) {
        ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestArmor.getEnchantmentLevel(ModEnchantments.REGURGITATION.get()) > 0) {
            RegurgitationHandler.regurgitate(player);
        }
    }
}