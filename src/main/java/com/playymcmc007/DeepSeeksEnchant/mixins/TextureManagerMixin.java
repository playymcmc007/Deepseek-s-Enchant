package com.playymcmc007.DeepSeeksEnchant.mixins;

import com.playymcmc007.DeepSeeksEnchant.enchantment.VisualImpairmentCurseEnchantment;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    //紫黑图片替代
    private void onGetTexture(ResourceLocation location, CallbackInfoReturnable<AbstractTexture> cir) {
        if(VisualImpairmentCurseEnchantment.isEffectActive() &&
                !location.equals(MissingTextureAtlasSprite.getLocation())) {

            cir.setReturnValue(MissingTextureAtlasSprite.getTexture());

        }
    }
}