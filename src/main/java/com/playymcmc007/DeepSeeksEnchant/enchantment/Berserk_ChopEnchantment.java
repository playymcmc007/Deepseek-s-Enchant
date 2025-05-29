package com.playymcmc007.DeepSeeksEnchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Berserk_ChopEnchantment extends Enchantment {
    private static final EnchantmentCategory AXE_ONLY = EnchantmentCategory.create(
            "AXE_ONLY",
            item -> item instanceof AxeItem
    );
    public Berserk_ChopEnchantment() {
        super(Rarity.VERY_RARE, AXE_ONLY, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinCost(int level) {
        return 32;
    }

    @Override
    public int getMaxCost(int level) {
        return 37;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof AxeItem;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getTarget() instanceof LivingEntity)) {
            return;
        }
        Player player = (Player) event.getEntity();
        LivingEntity target = (LivingEntity) event.getTarget();

        if (!player.onGround()) return;

        ItemStack weapon = player.getMainHandItem();
        if (weapon.getEnchantmentLevel(ModEnchantments.BERSERK_CHOP.get()) <= 0) return;
        if (player.getAttackStrengthScale(1.0F) < 0.9F) return;

        Level level = player.level();
        Vec3 lookVec = player.getLookAngle().normalize();
        Vec3 startPos = player.getEyePosition(1.0F);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.7F);

        DamageSource damageSource = player.damageSources().playerAttack(player);
        float baseDamage = 9.0f;
        int enchantLevel = weapon.getEnchantmentLevel(ModEnchantments.BERSERK_CHOP.get());

        double range = 5.0;
        double step = 0.5;
        double width = 1.5;

        for (double distance = 1.0; distance <= range; distance += step) {
            Vec3 checkPos = startPos.add(lookVec.scale(distance));

            if (level instanceof ServerLevel serverLevel) {

                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                        checkPos.x, checkPos.y, checkPos.z,
                        3, width, width, width, 0);
            }

            AABB hitBox = new AABB(
                    checkPos.x - width, checkPos.y - width, checkPos.z - width,
                    checkPos.x + width, checkPos.y + width, checkPos.z + width
            );

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, hitBox)) {
                if (entity == player || entity == target || player.isAlliedTo(entity)) continue;

                float distanceFactor = (float)(1.0 - (distance / range) * 0.5); // 50%伤害衰减
                float damage = baseDamage * 0.5F * (1 + enchantLevel) * distanceFactor;
                entity.hurt(damageSource, damage);

                if (level.random.nextFloat() < 0.3F) {
                    AABB secondaryBox = new AABB(
                            entity.getX() - 2, entity.getY() - 2, entity.getZ() - 2,
                            entity.getX() + 2, entity.getY() + 2, entity.getZ() + 2
                    );

                    for (LivingEntity secondaryEntity : level.getEntitiesOfClass(LivingEntity.class, secondaryBox)) {
                        if (secondaryEntity != entity && secondaryEntity != player && !player.isAlliedTo(secondaryEntity)) {
                            secondaryEntity.hurt(damageSource, baseDamage * 0.2F * (1 + enchantLevel));
                        }
                    }
                }
            }
        }
    }
}