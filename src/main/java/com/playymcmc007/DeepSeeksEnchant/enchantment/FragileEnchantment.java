package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FragileEnchantment extends Enchantment {
    public static final String FRAGILE_TAG = "FragileDurability";
    public static final int MAX_DURABILITY = 500;

    public FragileEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.VANISHABLE,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return !stack.isDamageableItem();
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    private static void processDurability(ItemStack stack, Player player, InteractionHand hand) {
        if (stack.isEmpty() || !EnchantmentToggleConfig.FRAGILE_ENABLED.get()) return;

        int currentDamage = getCurrentDamage(stack) + 1;
        setDamage(stack, currentDamage);

        if (currentDamage >= MAX_DURABILITY) {
            stack.shrink(1);
            player.broadcastBreakEvent(hand);
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().level().isClientSide) return;
        ItemStack stack = event.getItemStack();
        if (stack.getEnchantmentLevel(ModEnchantments.FRAGILE.get()) > 0) {
            processDurability(stack, event.getEntity(), event.getHand());
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide) return;
        ItemStack stack = event.getEntity().getMainHandItem();
        if (stack.getEnchantmentLevel(ModEnchantments.FRAGILE.get()) > 0) {
            processDurability(stack, event.getEntity(), InteractionHand.MAIN_HAND);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().level().isClientSide) return;
        ItemStack stack = event.getPlayer().getMainHandItem();
        if (stack.getEnchantmentLevel(ModEnchantments.FRAGILE.get()) > 0) {
            processDurability(stack, event.getPlayer(), InteractionHand.MAIN_HAND);
        }
    }

    public static int getCurrentDamage(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(FRAGILE_TAG) ? tag.getInt(FRAGILE_TAG) : 0;
    }

    public static void setDamage(ItemStack stack, int damage) {
        stack.getOrCreateTag().putInt(FRAGILE_TAG, damage);
        if (stack.isDamageableItem()) {
            stack.setDamageValue(damage);
        }
    }
}