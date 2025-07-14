package com.playymcmc007.DeepSeeksEnchant.mixins;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import com.playymcmc007.DeepSeeksEnchant.enchantment.FragileEnchantment;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void fixMaxDurability(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack)(Object)this;
        if (EnchantmentToggleConfig.FRAGILE_ENABLED.get() &&
                FragileEnchantment.getCurrentDamage(self) > 0) {
            cir.setReturnValue(FragileEnchantment.MAX_DURABILITY);
        }
    }

    @Inject(method = "getDamageValue", at = @At("HEAD"), cancellable = true)
    private void fixDamageValue(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack)(Object)this;
        if (EnchantmentToggleConfig.FRAGILE_ENABLED.get()) {
            int damage = FragileEnchantment.getCurrentDamage(self);
            if (damage > 0) {
                cir.setReturnValue(damage);
            }
        }
    }

    @Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
    private void forceDamageable(CallbackInfoReturnable<Boolean> cir) {
        ItemStack self = (ItemStack)(Object)this;
        if (EnchantmentToggleConfig.FRAGILE_ENABLED.get() &&
                FragileEnchantment.getCurrentDamage(self) > 0) {
            cir.setReturnValue(true);
        }
    }
}