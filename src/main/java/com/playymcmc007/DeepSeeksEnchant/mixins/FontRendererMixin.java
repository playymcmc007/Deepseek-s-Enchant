package com.playymcmc007.DeepSeeksEnchant.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.playymcmc007.DeepSeeksEnchant.client.AntiGlyphStateHandler;
import com.playymcmc007.DeepSeeksEnchant.enchantment.AntiGlyphCurseEnchantment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public abstract class FontRendererMixin {

    @Inject(
            method = {
                    "renderText(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F",
                    "renderText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F"
            },
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderText(CallbackInfoReturnable<Float> cir) {
        if (AntiGlyphStateHandler.isActive() && Minecraft.getInstance().level != null) {
            cir.setReturnValue(0.0F);
            cir.cancel();
        }
    }

    @Inject(
            method = "renderChar",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderChar(
            BakedGlyph glyph,
            boolean bold,
            boolean italic,
            float boldOffset,
            float x,
            float y,
            Matrix4f matrix,
            VertexConsumer buffer,
            float red,
            float green,
            float blue,
            float alpha,
            int packedLight,
            CallbackInfo ci
    ) {
        if (AntiGlyphStateHandler.isActive()) {
            ci.cancel();
        }
    }
}