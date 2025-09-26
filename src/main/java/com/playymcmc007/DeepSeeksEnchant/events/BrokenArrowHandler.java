package com.playymcmc007.DeepSeeksEnchant.events;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BrokenArrowHandler {

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        Player player = event.getEntity();
        ItemStack weapon = event.getBow();
        if (!EnchantmentToggleConfig.BROKEN_ARROW_ENABLED.get()) {
            return;
        }
        if (!weapon.isEmpty() && (weapon.getItem() instanceof BowItem || weapon.getItem() instanceof CrossbowItem)) {
            int level = weapon.getEnchantmentLevel(ModEnchantments.BROKEN_ARROW.get());
            if (level > 0) {
                if (player.getRandom().nextFloat() < 0.25f * level) {
                    event.setCanceled(true);

                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_BREAK,
                            net.minecraft.sounds.SoundSource.PLAYERS,
                            0.5F, 1.2F + player.getRandom().nextFloat() * 0.2F);

                    if (weapon.getItem() instanceof CrossbowItem) {
                        clearCrossbowState(weapon);
                    }
                    else if (weapon.getItem() instanceof BowItem && !player.getAbilities().instabuild) {
                        consumeArrowFromInventory(player);
                    }
                }
            }
        }
    }

    private static void clearCrossbowState(ItemStack crossbow) {
        if (crossbow.getItem() instanceof CrossbowItem) {
            crossbow.getOrCreateTag().remove("Charged");
            crossbow.getOrCreateTag().remove("ChargedProjectiles");
            crossbow.getOrCreateTag().remove("projectile_id");
            crossbow.getOrCreateTag().remove("projectile_count");
        }
    }

    private static void consumeArrowFromInventory(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof ArrowItem) {
                stack.shrink(1);
                break;
            }
        }
    }
}