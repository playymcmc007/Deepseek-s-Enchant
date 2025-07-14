package com.playymcmc007.DeepSeeksEnchant.client;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "deepseeksenchant", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class IlliteracyEffectHandler {
    private static final Random RANDOM = new Random();
    private static boolean isEffectActive = false;

    private static boolean hasIlliteracyCurse(Player player) {
        if (player == null || !EnchantmentToggleConfig.ILLITERACY_ENABLED.get()) {
            return false;
        }
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ILLITERACY.get(), helmet) > 0;
    }

    private static Component obfuscateText(Component original) {
        String text = original.getString();
        StringBuilder obfuscated = new StringBuilder();

        for (char c : text.toCharArray()) {
            // 保留空格(0x20)和换行符等空白字符
            if (Character.isWhitespace(c)) {
                obfuscated.append(c);
                continue;
            }

            // 生成随机BMP字符(0x0000-0xFFFF)
            char randomChar;
            do {
                // 生成随机字符(0x0021-0xFFFD)
                randomChar = (char) (RANDOM.nextInt(0xFFFD - 0x0021 + 1) + 0x0021);

                // 排除条件:
                // 1. 控制字符(虽然0x0021-0x007F已经排除了大部分)
                // 2. 未定义字符(根据Character.isDefined)
                // 3. 代理对字符(0xD800-0xDFFF)
                // 4. 其他特定排除字符(如零宽空格等)
            } while (Character.isISOControl(randomChar) ||
                    !Character.isDefined(randomChar) ||
                    (randomChar >= 0xD800 && randomChar <= 0xDFFF) ||
                    isZeroWidthSymbol(randomChar));

            obfuscated.append(randomChar);
        }

        return Component.literal(obfuscated.toString())
                .withStyle(original.getStyle().withObfuscated(true));
    }

    // 检查零宽符号(可根据需要扩展)
    private static boolean isZeroWidthSymbol(char c) {
        return c == '\u200B' ||  // 零宽空格
                c == '\u200C' ||  // 零宽非连接符
                c == '\u200D' ||  // 零宽连接符
                c == '\uFEFF';   // 零宽无断空格
    }
    //物品Tooltip
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!EnchantmentToggleConfig.ILLITERACY_ENABLED.get() || !isEffectActive) {
            return;
        }
        event.getToolTip().replaceAll(IlliteracyEffectHandler::obfuscateText);
    }
    //命名实体
    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        if (!EnchantmentToggleConfig.ILLITERACY_ENABLED.get() || !isEffectActive) {
            return;
        }
        event.setContent(obfuscateText(event.getContent()));
    }
    //聊天框
    @SubscribeEvent
    public static void onChatMessage(ClientChatReceivedEvent event) {
        if (isEffectActive && EnchantmentToggleConfig.ILLITERACY_ENABLED.get()) {
            event.setMessage(obfuscateText(event.getMessage()));
        }
    }

    //生效
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // 只有配置启用时才更新效果状态
            isEffectActive = EnchantmentToggleConfig.ILLITERACY_ENABLED.get()
                    && hasIlliteracyCurse(event.player);
        }
    }
    //屏蔽快捷栏物品名字
    @SubscribeEvent
    public static void cancelItemNameRender(RenderGuiOverlayEvent.Pre event) {
        if (isEffectActive && EnchantmentToggleConfig.ILLITERACY_ENABLED.get()
                && event.getOverlay() == VanillaGuiOverlay.ITEM_NAME.type()) {
            event.setCanceled(true);
        }
    }
}