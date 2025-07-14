package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import com.playymcmc007.DeepSeeksEnchant.util.EnchantmentDisguiseHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DisguiseAndDeceptionCurseEnchantment extends Enchantment {
    public DisguiseAndDeceptionCurseEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND,
                        EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                        EquipmentSlot.LEGS, EquipmentSlot.FEET});
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getMinCost(int level) {
        return 25;
    }

    @Override
    public int getMaxCost(int level) {
        return 50;
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
    public boolean isTradeable() {
        return false;
    }
    @Override
    public boolean isDiscoverable() {
        return false;
    }
    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public Component getFullname(int level) {
        boolean showOriginal = !EnchantmentToggleConfig.DISGUISE_AND_DECEPTION_ENABLED.get();
        return EnchantmentDisguiseHelper.getDisguisedName(
                super.getFullname(level),
                level,
                showOriginal
        );
    }

    @SubscribeEvent
    public void onItemUsed(PlayerEvent.BreakSpeed event) {
        if (!EnchantmentToggleConfig.DISGUISE_AND_DECEPTION_ENABLED.get()) {
            return;
        }
        ItemStack stack = event.getEntity().getMainHandItem();
        if (stack != null && stack.isDamaged()) {
            checkDurabilityAndReveal(stack, event.getEntity());
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent event) {
        if (!EnchantmentToggleConfig.DISGUISE_AND_DECEPTION_ENABLED.get()) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity entity) {
            for (ItemStack stack : entity.getArmorSlots()) {
                if (stack != null && stack.isDamaged()) {
                    checkDurabilityAndReveal(stack, entity);
                }
            }
            checkDurabilityAndReveal(entity.getMainHandItem(), entity);
            checkDurabilityAndReveal(entity.getOffhandItem(), entity);
        }
    }
    @SubscribeEvent
    public void onPlayerMine(PlayerEvent.BreakSpeed event) {
        if (!EnchantmentToggleConfig.DISGUISE_AND_DECEPTION_ENABLED.get()) {
            return;
        }
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        checkDurabilityAndReveal(stack, player);
    }
    private void checkDurabilityAndReveal(ItemStack stack, LivingEntity entity) {
        if (stack == null || stack.isEmpty()) return;

        if (!EnchantmentHelper.getEnchantments(stack).containsKey(this)) return;
        if (stack.isDamageableItem() && stack.getDamageValue() > 0) {
            revealCurse(stack, entity);
        }
    }

    private void revealCurse(ItemStack stack, LivingEntity entity) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag().copy();
            int damage = stack.getDamageValue();
            boolean unbreakable = tag.getBoolean("Unbreakable");
            stack.setTag(null);
            if (stack.isDamageableItem()) {
                stack.setDamageValue(damage);
                if (unbreakable) {
                    stack.getOrCreateTag().putBoolean("Unbreakable", true);
                }
            }
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.displayClientMessage(
                    Component.translatable("message.deepseeksenchant.deception.tip").withStyle(ChatFormatting.RED),
                    true
            );
        }
        stack.setHoverName(Component.translatable("message.deepseeksenchant.deception.name").withStyle(ChatFormatting.RED));
    }
}