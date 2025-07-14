package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StrangeNoisesCurseEnchantment extends Enchantment {
    private static final Random RANDOM = new Random();
    private static List<SoundEvent> ALL_SOUND_EVENTS = null;
    private static final Map<UUID, double[]> PLAYER_LAST_POS = new ConcurrentHashMap<>();
    private static final Map<UUID, double[]> MOB_LAST_POS = new ConcurrentHashMap<>();

    private static List<SoundEvent> getAllSoundEvents() {
        if (ALL_SOUND_EVENTS == null) {
            ALL_SOUND_EVENTS = new ArrayList<>();
            ForgeRegistries.SOUND_EVENTS.getValues().forEach(ALL_SOUND_EVENTS::add);
        }
        return ALL_SOUND_EVENTS;
    }

    private static SoundEvent getRandomSoundEvent() {
        List<SoundEvent> sounds = getAllSoundEvents();
        if (sounds.isEmpty()) {
            return SoundEvents.EMPTY;
        }
        return sounds.get(RANDOM.nextInt(sounds.size()));
    }


    public StrangeNoisesCurseEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 25;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }


    @Mod.EventBusSubscriber
    public static class SoundCurseHandler {
        private static final double MOVE_THRESHOLD = 0.2;

        @SubscribeEvent
        // 玩家
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (!EnchantmentToggleConfig.STRANGE_NOISES_ENABLED.get()) {
                return;
            }
            if (event.phase != TickEvent.Phase.END) return;

            Player player = event.player;
            UUID uuid = player.getUUID();

            if (player.getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(ModEnchantments.STRANGE_NOISES_CURSE.get()) > 0) {
                double[] lastPos = PLAYER_LAST_POS.computeIfAbsent(uuid, k -> new double[]{
                        player.getX(), player.getZ()
                });

                double dx = player.getX() - lastPos[0];
                double dz = player.getZ() - lastPos[1];

                if (dx*dx + dz*dz > MOVE_THRESHOLD * MOVE_THRESHOLD) {
                    playRandomSound(player);
                    lastPos[0] = player.getX();
                    lastPos[1] = player.getZ();
                }
            } else {
                PLAYER_LAST_POS.remove(uuid);
            }
        }

        @SubscribeEvent
        // 生物
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            if (!EnchantmentToggleConfig.STRANGE_NOISES_ENABLED.get()) {
                return;
            }
            LivingEntity entity = event.getEntity();
            if (entity instanceof Player) return;

            UUID uuid = entity.getUUID();

            if (entity.getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(ModEnchantments.STRANGE_NOISES_CURSE.get()) > 0) {
                double[] lastPos = MOB_LAST_POS.computeIfAbsent(uuid, k -> new double[]{
                        entity.getX(), entity.getZ()
                });

                double dx = entity.getX() - lastPos[0];
                double dz = entity.getZ() - lastPos[1];

                if (dx*dx + dz*dz > MOVE_THRESHOLD * MOVE_THRESHOLD) {
                    playRandomSound(entity);
                    lastPos[0] = entity.getX();
                    lastPos[1] = entity.getZ();
                }
            } else {
                MOB_LAST_POS.remove(uuid);
            }
        }

        private static void playRandomSound(LivingEntity entity) {
            float randomPitch = 0.5f + RANDOM.nextFloat() * (2.0f - 0.5f);
            entity.playSound(getRandomSoundEvent(), 1.0F, randomPitch);
        }
    }
}