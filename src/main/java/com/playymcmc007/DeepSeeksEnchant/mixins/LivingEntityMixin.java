package com.playymcmc007.DeepSeeksEnchant.mixins;

import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "isDamageSourceBlocked",
            at = @At("HEAD"),
            cancellable = true
    )
    //盾逻辑
    private void onIsDamageSourceBlocked(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;
        ItemStack shield = entity.getUseItem();

        if (shield.getItem() instanceof ShieldItem &&
                EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.OMNIGUARD.get(), shield) > 0) {
            cir.setReturnValue(true);
        }
    }
}