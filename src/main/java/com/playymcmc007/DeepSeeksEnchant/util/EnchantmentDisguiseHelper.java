package com.playymcmc007.DeepSeeksEnchant.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EnchantmentDisguiseHelper {
    private static final Random RANDOM = new Random();

    public static Component getDisguisedName(Component originalName, int level, boolean forceOriginal) {
        if (forceOriginal) {
            return originalName;
        }

        List<Enchantment> validEnchantments = BuiltInRegistries.ENCHANTMENT.stream()
                .filter(e -> !e.isCurse())
                .collect(Collectors.toList());

        if (!validEnchantments.isEmpty()) {
            Enchantment disguised = validEnchantments.get(RANDOM.nextInt(validEnchantments.size()));
            return disguised.getFullname(level);
        }
        return originalName;
    }
}