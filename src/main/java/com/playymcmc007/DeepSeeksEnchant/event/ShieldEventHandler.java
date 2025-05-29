package com.playymcmc007.DeepSeeksEnchant.event;

import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import com.playymcmc007.DeepSeeksEnchant.enchantment.OmniGuardEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "deepseeksenchant")
public class ShieldEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().isBlocking()) {
            DamageSource source = event.getSource();
            if (source.is(DamageTypes.ARROW) ||
                    source.is(DamageTypes.TRIDENT) ||
                    source.is(DamageTypes.MOB_PROJECTILE)){

                ItemStack shield = event.getEntity().getUseItem();
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.OMNIGUARD.get(), shield);

                if (level > 0) {
                    event.setAmount(event.getAmount() * 0.3f);
                    int damage = (int)(event.getAmount() * OmniGuardEnchantment.getDurabilityMultiplier());
                    shield.hurtAndBreak(damage, event.getEntity(),
                            e -> e.broadcastBreakEvent(e.getUsedItemHand()));
                }
            }
        }
    }
}