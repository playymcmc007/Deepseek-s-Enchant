package com.playymcmc007.DeepSeeksEnchant.client;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class AntiGlyphStateHandler {
    private static boolean active = false;

    // 更新状态（含配置检查）
    public static void updateState(boolean hasEnchant) {
        active = hasEnchant && EnchantmentToggleConfig.ANTI_GLYPH_ENABLED.get();
    }

    // 状态重置（统一使用reset）
    public static void reset() {
        active = false;
    }

    // 状态检查
    public static boolean isActive() {
        return active;
    }
}