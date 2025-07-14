package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.client.AntiGlyphStateHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "deepseeksenchant", value = Dist.CLIENT)
public class AntiGlyphCurseEnchantment extends Enchantment {

    public AntiGlyphCurseEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player != null) {
            boolean hasEnchant = EnchantmentHelper.getEnchantmentLevel(
                    ModEnchantments.ANTI_GLYPH.get(),
                    event.player
            ) > 0;
            AntiGlyphStateHandler.updateState(hasEnchant);
        }
    }

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        AntiGlyphStateHandler.reset();
    }

    @SubscribeEvent
    public static void onDimensionChange(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof Player) {
            AntiGlyphStateHandler.reset();
        }
    }

}