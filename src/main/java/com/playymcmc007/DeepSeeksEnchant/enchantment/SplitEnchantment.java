package com.playymcmc007.DeepSeeksEnchant.enchantment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
@Mod.EventBusSubscriber
public class SplitEnchantment extends Enchantment {
    private static final Map<UUID, List<SplitTarget>> PLAYER_TARGETS = new HashMap<>();
    private static final Map<UUID, Integer> PLAYER_TIMERS = new HashMap<>();
    public SplitEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 2;
    }
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 2;
    }
    @Override
    public int getMaxLevel() {
        return 3;
    }
    @Override
    public boolean isTreasureOnly() {
        return true;
    }
    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }
    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (!attacker.level().isClientSide && target instanceof LivingEntity livingTarget) {
            UUID playerId = attacker.getUUID();
            if (!PLAYER_TARGETS.containsKey(playerId)) {
                PLAYER_TARGETS.put(playerId, new ArrayList<>());
            }
            PLAYER_TARGETS.get(playerId).add(new SplitTarget(livingTarget, false));
            PLAYER_TIMERS.put(playerId, getTickDelay(level));
        }
    }
    private static LivingEntity createEntityWithNBT(LivingEntity original) {
        LivingEntity newEntity = (LivingEntity) original.getType().create(original.level());
        if (newEntity != null) {
            CompoundTag nbt = new CompoundTag();
            original.saveWithoutId(nbt);
            nbt.remove("UUID");
            newEntity.load(nbt);
            newEntity.copyPosition(original);
            newEntity.setHealth(original.getHealth());
        }
        return newEntity;
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            UUID playerId = event.player.getUUID();
            if (PLAYER_TIMERS.containsKey(playerId)) {
                int timer = PLAYER_TIMERS.get(playerId);
                if (timer <= 0) {
                    List<SplitTarget> targets = PLAYER_TARGETS.getOrDefault(playerId, new ArrayList<>());
                    List<SplitTarget> newTargets = new ArrayList<>();
                    int level = event.player.getMainHandItem().getEnchantmentLevel(ModEnchantments.SPLIT.get());
                    for (SplitTarget splitTarget : targets) {
                        LivingEntity target = splitTarget.entity();
                        boolean hasSplit = splitTarget.hasSplit();
                        if (target.isAlive()) {
                            if (!hasSplit) {
                                for (int i = 0; i < 2; i++) {
                                    LivingEntity newEntity = createEntityWithNBT(target);
                                    if (newEntity != null) {
                                        event.player.level().addFreshEntity(newEntity);
                                        newTargets.add(new SplitTarget(newEntity, false));
                                    }
                                }
                                newTargets.add(new SplitTarget(target, true));
                            } else {
                                newTargets.add(splitTarget);
                            }
                        }
                    }
                    PLAYER_TARGETS.put(playerId, newTargets);
                    PLAYER_TIMERS.put(playerId, getTickDelay(level));
                } else {
                    PLAYER_TIMERS.put(playerId, timer - 1);
                }
            }
        }
    }
    private static int getTickDelay(int level) {
        return (int) (40 * Math.pow(0.5, level - 1));
    }
    private record SplitTarget(LivingEntity entity, boolean hasSplit) {}
}