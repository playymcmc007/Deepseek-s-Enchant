package com.playymcmc007.DeepSeeksEnchant.client;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SnipeCooldownRenderer {
    private static final int COOLDOWN_COLOR = 0x8047FF47;
    private static ItemStack lastCoolingWeapon = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            ItemStack weapon = player.getMainHandItem();
            CompoundTag tag = weapon.getTag();
            if (tag != null && tag.contains("CooldownEndTick")) {
                lastCoolingWeapon = weapon;
            } else if (weapon.equals(lastCoolingWeapon)) {
                lastCoolingWeapon = null;
            }
        }
    }

    @SubscribeEvent
    public static void onPreRender(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id() == VanillaGuiOverlay.HOTBAR.id() &&
                lastCoolingWeapon != null) {
        }
    }
    @SubscribeEvent
    public static void onPostRender(RenderGuiOverlayEvent.Post event) {
        if (Minecraft.getInstance().isPaused() ||
                event.getOverlay().id() != VanillaGuiOverlay.HOTBAR.id()) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack currentWeapon = player.getMainHandItem();
        if (currentWeapon.isEmpty()) {
            lastCoolingWeapon = null;
            return;
        }

        if (lastCoolingWeapon == null || !ItemStack.matches(currentWeapon, lastCoolingWeapon)) {
            lastCoolingWeapon = null;
            return;
        }

        CompoundTag tag = lastCoolingWeapon.getTag();
        if (tag == null || !tag.contains("CooldownEndTick") || !tag.contains("CooldownTicks")) {
            lastCoolingWeapon = null;
            return;
        }

        long remainingTicks = tag.getLong("CooldownEndTick") - player.level().getGameTime();
        if (remainingTicks <= 0) {
            lastCoolingWeapon = null;
            return;
        }

        int totalTicks = tag.getInt("CooldownTicks");
        float progress = totalTicks > 0 ? (float) remainingTicks / totalTicks : 0f;

        String text = String.format("%.1fs", remainingTicks / 20f);
        renderCustomOverlay(event.getGuiGraphics(), event.getWindow(), progress, text);
    }


    private static void renderCustomOverlay(GuiGraphics gui, Window window, float progress, String text) {

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        int hotbarWidth = 182;
        int hotbarHeight = 22;
        int hotbarX = (window.getGuiScaledWidth() - hotbarWidth) / 2;
        int hotbarY = window.getGuiScaledHeight() - hotbarHeight;

        int slotSize = 20;
        int selectedSlot = player.getInventory().selected;
        int slotX = hotbarX + selectedSlot * slotSize;

        int innerX = slotX + 3;
        int innerY = hotbarY + 3;
        int innerSize = 16;

        int progressHeight = (int)(innerSize * (1 - progress));
        gui.fill(
                innerX, innerY + progressHeight,
                innerX + innerSize, innerY + innerSize,
                COOLDOWN_COLOR
        );

        renderCrosshairText(gui, window, text);

    }

    private static void renderCrosshairText(GuiGraphics gui, Window window, String text) {
        Minecraft mc = Minecraft.getInstance();

        int centerX = window.getGuiScaledWidth() / 2;
        int centerY = window.getGuiScaledHeight() / 2;

        int textX = centerX - mc.font.width(text) / 2;
        int textY = centerY + 20;

        gui.drawString(
                mc.font,
                text,
                textX + 1, textY + 1,
                0x000000,
                false
        );
        gui.drawString(
                mc.font,
                text,
                textX, textY,
                0xFFFFFF,
                false
        );
    }
    @SubscribeEvent
    public static void onHotbarChange(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player == Minecraft.getInstance().player) {
            ItemStack newWeapon = event.player.getMainHandItem();

            if (lastCoolingWeapon != null && !ItemStack.matches(newWeapon, lastCoolingWeapon)) {
                lastCoolingWeapon = null;
            }
        }
    }
}