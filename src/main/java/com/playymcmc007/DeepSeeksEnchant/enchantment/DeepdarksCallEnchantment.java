package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.warden.Warden;

public class DeepdarksCallEnchantment extends Enchantment {
    private static final int DETECTION_RANGE = 49;

    public DeepdarksCallEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 30;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 60;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    public static void onPlayerMove(Player player, Level level) {
        if (!EnchantmentToggleConfig.DEEPDARKSCALL_ENABLED.get()) {
            return;
        }
        if (level.isClientSide) return;

        if (player.getInventory().getArmor(1).getEnchantmentLevel(ModEnchantments.DEEPDARKSCALL.get()) > 0) {
            angerNearbyWardens(player, level, DETECTION_RANGE);

            activateNearbySculkShriekers(player, level);
        }
    }

    private static void activateNearbySculkShriekers(Player player, Level level) {
        BlockPos playerPos = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-16, -16, -16),
                playerPos.offset(16, 16, 16))) {

            if (level.getBlockEntity(pos) instanceof SculkShriekerBlockEntity shrieker) {
                BlockState state = level.getBlockState(pos);

                if (state.hasProperty(SculkShriekerBlock.CAN_SUMMON) &&
                        !state.getValue(SculkShriekerBlock.CAN_SUMMON)) {
                    level.setBlock(pos, state.setValue(SculkShriekerBlock.CAN_SUMMON, true), 3);
                }

                if (state.getValue(SculkShriekerBlock.CAN_SUMMON)) {
                    if (shrieker.getLevel() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 4; i++) {
                            shrieker.tryShriek(serverLevel, null);
                        }
                        shrieker.tryRespond(serverLevel);
                    }
                }
            }
        }
    }

    public static void angerNearbyWardens(Player player, Level level, int range) {
        for (Warden warden : level.getEntitiesOfClass(
                Warden.class,
                player.getBoundingBox().inflate(range),
                warden -> warden.isAlive()
        )) {
            warden.increaseAngerAt(player, AngerLevel.ANGRY.getMinimumAnger(), true);
        }
    }

}