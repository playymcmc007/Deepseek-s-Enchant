package com.playymcmc007.DeepSeeksEnchant.item;

import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateDeceptionItem extends Item {
    public GenerateDeceptionItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        if (!world.isClientSide) {
            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);

            EnchantmentHelper.setEnchantments(
                    Map.of(ModEnchantments.DISGUISE_AND_DECEPTION.get(), 1),
                    enchantedBook
            );

            if (!player.getInventory().add(enchantedBook)) {
                player.drop(enchantedBook, false);
            }
            itemInHand.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, world.isClientSide());
    }
}