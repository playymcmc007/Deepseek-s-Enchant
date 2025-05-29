package com.playymcmc007.DeepSeeksEnchant.enchantment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class IronHeadEnchantment extends Enchantment {
    public IronHeadEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 5 + 5 * (enchantmentLevel - 1);
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 30;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Mod.EventBusSubscriber
    public static class IronHeadHandler {
        private static final int COOLDOWN_TICKS = 5; // 撞击冷却时间

        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent event) {
            Player player = event.player;
            Level world = player.level();

            BlockPos headPos = BlockPos.containing(
                    player.getX(),
                    player.getY() + player.getEyeHeight(),
                    player.getZ()
            );
            BlockPos targetPos = headPos.above();
            BlockState targetState = world.getBlockState(targetPos);

            if (targetState.isAir() ||
                    player.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(ModEnchantments.IRON_HEAD.get()) <= 0) {
                return;
            }

            AABB headAABB = new AABB(
                    player.getX() - 0.3,
                    player.getY() + player.getEyeHeight() - 0.2,
                    player.getZ() - 0.3,
                    player.getX() + 0.3,
                    player.getY() + player.getEyeHeight() + 0.2,
                    player.getZ() + 0.3
            );

            boolean isColliding = !targetState.getCollisionShape(world, targetPos).isEmpty() &&
                    headAABB.intersects(new AABB(targetPos));

            if (isColliding) {
                int level = player.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(ModEnchantments.IRON_HEAD.get());

                if (canBreakBlock(targetState, world, targetPos, level)) {

                    world.playSound(
                            null,
                            targetPos,
                            targetState.getSoundType().getBreakSound(),
                            SoundSource.BLOCKS,
                            1.0F,
                            0.8F + world.random.nextFloat() * 0.4F
                    );
                    world.destroyBlock(targetPos, false);

                    player.setDeltaMovement(player.getDeltaMovement().add(0, -0.1, 0));
                } else {
                    world.playSound(
                            null,
                            targetPos,
                            SoundEvents.ANVIL_LAND,
                            SoundSource.PLAYERS,
                            0.7F,
                            0.5F + world.random.nextFloat() * 0.5F
                    );

                    if (world.isClientSide) {
                        for (int i = 0; i < 5; i++) {
                            world.addParticle(
                                    ParticleTypes.LAVA,
                                    targetPos.getX() + 0.5,
                                    targetPos.getY() + 0.5,
                                    targetPos.getZ() + 0.5,
                                    world.random.nextGaussian() * 0.02,
                                    world.random.nextGaussian() * 0.02,
                                    world.random.nextGaussian() * 0.02
                            );
                        }
                    }

                    player.setDeltaMovement(player.getDeltaMovement().add(0, -0.2, 0));
                    player.hurtMarked = true;
                }

                player.getCooldowns().addCooldown(player.getItemBySlot(EquipmentSlot.HEAD).getItem(), COOLDOWN_TICKS);
            }
        }

        private static boolean canBreakBlock(BlockState state, Level world, BlockPos pos, int enchantLevel) {
            float hardness = state.getDestroySpeed(world, pos);

            if (hardness < 0) return false;

            float maxHardness = 12.0f * enchantLevel - 10.0f;
            return hardness <= maxHardness;
        }
    }
}