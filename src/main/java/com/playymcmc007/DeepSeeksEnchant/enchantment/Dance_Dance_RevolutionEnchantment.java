package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class Dance_Dance_RevolutionEnchantment extends Enchantment {
    private static final Map<UUID, DDRComboData> PLAYER_COMBOS = new HashMap<>();
    private static final int BASE_TICKS = 60;
    private static final int TICK_DECREMENT = 5;

    public Dance_Dance_RevolutionEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 25;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }
    private static class DDRComboData {
        int level = 0;
        List<DDRDirection> sequence = new ArrayList<>();
        int currentStep = 0;
        int remainingTicks = BASE_TICKS;
        boolean active = false;

        void reset() {
            level = 0;
            sequence.clear();
            currentStep = 0;
            remainingTicks = BASE_TICKS;
            active = false;
        }

        void startNewCombo() {
            level++;
            sequence.clear();
            currentStep = 0;
            remainingTicks = Math.max(20, BASE_TICKS - (level * TICK_DECREMENT));
            generateSequence();
            active = true;
        }

        void generateSequence() {
            sequence.clear();
            int length = 1 + (level - 1) * 2;
            length = Math.max(1, length);
            length = Math.min(10, length);
            Random random = new Random();

            for (int i = 0; i < length; i++) {
                sequence.add(DDRDirection.values()[random.nextInt(DDRDirection.values().length)]);
            }
        }

        float getDamageMultiplier() {
            return 1.0f + ((level-1) * 0.2f);
        }
    }

    private enum DDRDirection {
        UP("↑", Minecraft.getInstance().options.keyUp),
        DOWN("↓", Minecraft.getInstance().options.keyDown),
        LEFT("←", Minecraft.getInstance().options.keyLeft),
        RIGHT("→", Minecraft.getInstance().options.keyRight);

        final String symbol;
        final KeyMapping key;

        DDRDirection(String symbol, KeyMapping key) {
            this.symbol = symbol;
            this.key = key;
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!EnchantmentToggleConfig.DDR_ENABLED.get()) {
            PLAYER_COMBOS.remove(event.player.getUUID());
            return;
        }
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;

        Player player = event.player;
        ItemStack mainHand = player.getMainHandItem();
        UUID playerId = player.getUUID();

        if (!hasEnchantment(mainHand)) {
            if (PLAYER_COMBOS.containsKey(playerId)) {
                PLAYER_COMBOS.remove(playerId);
            }
            return;
        }

        DDRComboData combo = PLAYER_COMBOS.computeIfAbsent(playerId, k -> new DDRComboData());

        if (!combo.active || combo.sequence.isEmpty()) {
            combo.startNewCombo();
        }

        combo.remainingTicks--;
        if (combo.remainingTicks <= 0) {
            combo.reset();
            sendMessage(player, "message.deepseeksenchant.combo.timeout", ChatFormatting.RED);
            playClientSound(
                    player,
                    SoundEvents.ITEM_BREAK,
                    1.0f,
                    2.0f
            );
            return;
        }

        if (player.tickCount % 3 == 0) {
            displayDDRInfo(player, combo);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onKeyInput(InputEvent.Key event) {
        if (!EnchantmentToggleConfig.DDR_ENABLED.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        Player player = minecraft.player;
        UUID playerId = player.getUUID();

        if (!PLAYER_COMBOS.containsKey(playerId)) return;

        DDRComboData combo = PLAYER_COMBOS.get(playerId);
        if (!combo.active) return;

        for (DDRDirection direction : DDRDirection.values()) {
            if (direction.key.consumeClick()) {
                playClientSound(
                        player,
                        SoundEvents.NOTE_BLOCK_SNARE.value(),
                        1.5f,
                        1.0f
                );
                if (direction == combo.sequence.get(combo.currentStep)) {
                    combo.currentStep++;
                    combo.remainingTicks = Math.max(20, BASE_TICKS - (combo.level * TICK_DECREMENT));

                    if (combo.currentStep >= combo.sequence.size()) {
                        playClientSound(
                                player,
                                SoundEvents.EXPERIENCE_ORB_PICKUP,
                                1.0f,
                                1.0f + (combo.level-1) * 0.15f
                        );
                        combo.startNewCombo();
                    }
                } else {
                    playClientSound(
                            player,
                            SoundEvents.ITEM_BREAK,
                            1.0f,
                            2.0f
                    );
                    combo.reset();
                    sendMessage(player, "message.deepseeksenchant.combo.wrong_input", ChatFormatting.RED);
                }
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!EnchantmentToggleConfig.DDR_ENABLED.get()) {
            return;
        }
        if (!(event.getSource().getDirectEntity() instanceof Player player)) return;

        DDRComboData combo = PLAYER_COMBOS.get(player.getUUID());
        if (combo != null && combo.active && combo.level > 1) {
            event.setAmount(event.getAmount() * combo.getDamageMultiplier());
            playClientSound(
                    player,
                    SoundEvents.NOTE_BLOCK_BELL.value(),
                    1.0f,
                    2.0f
            );
            combo.reset();
        }
    }

    private static boolean hasEnchantment(ItemStack stack) {
        return stack.getEnchantmentLevel(ModEnchantments.DDR.get()) > 0;
    }

    private static void displayDDRInfo(Player player, DDRComboData combo) {
        MutableComponent message = Component.literal("");

        for (int i = 0; i < combo.sequence.size(); i++) {
            DDRDirection direction = combo.sequence.get(i);

            ChatFormatting color = (i == combo.currentStep) ? ChatFormatting.GOLD :
                    (i < combo.currentStep) ? ChatFormatting.GREEN :
                            ChatFormatting.GRAY;

            message.append(Component.literal(direction.symbol).withStyle(color));

            if (i < combo.sequence.size() - 1) {
                message.append(" ");
            }
        }

        float progress = (float) combo.remainingTicks / (BASE_TICKS - (combo.level * TICK_DECREMENT));
        message.append(" ").append(createProgressBar(progress));

        player.displayClientMessage(message, true);
    }

    private static Component createProgressBar(float progress) {
        int total = 10;
        int filled = (int) (progress * total);

        MutableComponent bar = Component.literal("[");
        for (int i = 0; i < total; i++) {
            bar.append(Component.literal("|")
                    .withStyle(i < filled ? ChatFormatting.GREEN : ChatFormatting.RED));
        }
        bar.append(Component.literal("]"));

        return bar;
    }

    private static void sendMessage(Player player, String text, ChatFormatting color) {
        player.displayClientMessage(Component.translatable(text).withStyle(color), true);
    }

    private static void playSound(Player player, SoundEvent sound, float volume, float pitch) {
        if (!player.level().isClientSide) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    sound, player.getSoundSource(), volume, pitch);
        }
    }
    @OnlyIn(Dist.CLIENT)
    private static void playClientSound(Player player, SoundEvent sound, float volume, float pitch) {
        if (player.level().isClientSide) {
            Minecraft.getInstance().getSoundManager().play(
                    new SimpleSoundInstance(
                            sound,
                            player.getSoundSource(),
                            volume,
                            pitch,
                            player.getRandom(),
                            player.getX(),
                            player.getY(),
                            player.getZ()
                    )
            );
        }
    }
}