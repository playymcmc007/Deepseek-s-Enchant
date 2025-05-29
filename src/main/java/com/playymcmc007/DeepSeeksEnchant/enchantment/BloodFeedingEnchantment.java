package com.playymcmc007.DeepSeeksEnchant.enchantment;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

public class BloodFeedingEnchantment extends Enchantment {

    public BloodFeedingEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{});
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

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() == Items.POTION;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().getEnchantmentLevel(ModEnchantments.BLOOD_FEEDING.get()) > 0 ||
                event.getRight().getEnchantmentLevel(ModEnchantments.BLOOD_FEEDING.get()) > 0) {
            event.setCanceled(true);
        }
    }

    public static boolean canFeed(Entity entity) {
        if (!(entity instanceof TamableAnimal)) return false;

        if (entity instanceof Wolf wolf) {
            return !wolf.isInSittingPose();
        }
        if (entity instanceof Cat cat) {
            return !cat.isLying();
        }
        return true;
    }

    public static boolean feedPet(Player player, ItemStack stack, LivingEntity pet, boolean force) {
        if (!(pet instanceof TamableAnimal tamePet)) return false;
        if (!tamePet.isTame() || tamePet.getOwner() != player) return false;
        if (pet.getHealth() >= pet.getMaxHealth()) return false;
        if (!force && !canFeed(pet)) return false;

        player.getCooldowns().addCooldown(Items.POTION, 40);
        float cost = Math.max(1.0f, player.getMaxHealth() * 0.1f);
        float healAmount = cost * 1.5f;

        if (player.getHealth() > cost) {
            player.hurt(player.damageSources().magic(), cost);
            pet.heal(healAmount);
            player.swing(InteractionHand.MAIN_HAND);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5F, 0.8F);
            player.getCooldowns().addCooldown(Items.POTION, 40);
            return true;
        } else {
            pet.heal(player.getHealth() * 2.0f);
            player.hurt(player.damageSources().magic(), player.getHealth());
            return true;
        }
    }

    @Mod.EventBusSubscriber
    public static class BloodFeedingEvents {
        @SubscribeEvent
        public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
            Player player = event.getEntity();
            ItemStack stack = event.getItemStack();
            Entity target = event.getTarget();
            if (player.getCooldowns().isOnCooldown(Items.POTION)) {
                event.setCanceled(true);
                return;
            }
            if (stack.getEnchantmentLevel(ModEnchantments.BLOOD_FEEDING.get()) > 0 &&
                    target instanceof LivingEntity livingTarget) {

                boolean force = player.isShiftKeyDown();

                if (feedPet(player, stack, livingTarget, force)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}