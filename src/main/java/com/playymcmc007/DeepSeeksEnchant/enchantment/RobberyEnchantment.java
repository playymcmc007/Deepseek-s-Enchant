package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class RobberyEnchantment extends Enchantment {
    public RobberyEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.CROSSBOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public static void handleVillagerKill(LootParams params, ItemStack weapon) {
        if (!EnchantmentToggleConfig.ROBBERY_ENABLED.get()) {
            return;
        }
        if (!(params.getParameter(LootContextParams.THIS_ENTITY) instanceof Villager killedVillager) ||
                !(params.getParameter(LootContextParams.KILLER_ENTITY) instanceof Player player)) {
            return;
        }

        int level = weapon.getEnchantmentLevel(ModEnchantments.ROBBERY.get());
        if (level <= 0) return;

        int luckLevel = player.getEffect(MobEffects.LUCK) != null ?
                player.getEffect(MobEffects.LUCK).getAmplifier() + 1 : 0;

        Level world = killedVillager.level();
        if (world.isClientSide()) return;

        // 物品掉落逻辑
        var offers = killedVillager.getOffers();
        if (!offers.isEmpty()) {
            for (int i = 0; i < offers.size(); i++) {
                float probability = Math.min(0.5f + (luckLevel * 0.1f), 0.9f);
                float decayFactor = 1.0f / (i + 1);

                if (world.random.nextFloat() < probability * decayFactor) {
                    killedVillager.spawnAtLocation(offers.get(i).getResult().copy());
                }
            }
        } else {
            killedVillager.spawnAtLocation(new ItemStack(Items.EMERALD, 1));
        }

        resetVillagerReputation(player, killedVillager);

        List<Villager> nearbyVillagers = world.getEntitiesOfClass(
                Villager.class,
                killedVillager.getBoundingBox().inflate(32),
                v -> v != killedVillager
        );

        for (Villager nearbyVillager : nearbyVillagers) {
            resetVillagerReputation(player, nearbyVillager);
        }
    }

    private static void resetVillagerReputation(Player player, Villager villager) {
        //清除正面言论
        villager.getGossips().remove(player.getUUID(), GossipType.MAJOR_POSITIVE);
        villager.getGossips().remove(player.getUUID(), GossipType.MINOR_POSITIVE);
        villager.getGossips().remove(player.getUUID(), GossipType.TRADING);
        //添加极高的负面言论，数值实际上会锁上限（100、200），填高数值确保声望极低
        villager.getGossips().add(
                player.getUUID(),
                GossipType.MAJOR_NEGATIVE,
                10000
        );

        villager.getGossips().add(
                player.getUUID(),
                GossipType.MINOR_NEGATIVE,
                10000
        );

        if (!villager.getOffers().isEmpty()) {
            MerchantOffer firstOffer = villager.getOffers().get(0);
            villager.notifyTrade(firstOffer);
        }
    }
}