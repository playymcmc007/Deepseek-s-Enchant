package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GrenadierEnchantment extends Enchantment {
    private static final int COOLDOWN_TICKS = 20;

    public GrenadierEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 15;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 10;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other instanceof ProtectionEnchantment prot) {
            return prot.type != ProtectionEnchantment.Type.EXPLOSION &&
                    prot.type != ProtectionEnchantment.Type.FIRE;
        }
        if (other instanceof ThornsEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!EnchantmentToggleConfig.GRENADIER_ENABLED.get()) {
            return;
        }
        Player player = event.getEntity();
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        int level = chestplate.getEnchantmentLevel(ModEnchantments.GRENADIER.get());

        if (level <= 0) return;

        ItemStack heldItem = event.getItemStack();
        Level world = player.level();

        if (world.isClientSide()) return;

        if (heldItem.getItem() == Items.TNT) {
            if (isOnCooldown(player, Items.TNT)) return;

            if (player.isShiftKeyDown()) {
                int count = heldItem.getCount();
                for (int i = 0; i < count; i++) {
                    launchTnt(player, world, heldItem, level);
                }
                if (!player.isCreative()) {
                    heldItem.shrink(count);
                }
            } else {
                launchTnt(player, world, heldItem, level);
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
            }

            applyCooldown(player, Items.TNT);
            applyCooldown(player, Items.FIRE_CHARGE);
            event.setCanceled(true);
        }
        else if (heldItem.getItem() == Items.FIRE_CHARGE) {
            if (isOnCooldown(player, Items.FIRE_CHARGE)) return;

            if (player.isShiftKeyDown()) {
                int count = heldItem.getCount();
                for (int i = 0; i < count; i++) {
                    launchFireball(player, world, heldItem, level, true, i);
                }
                if (!player.isCreative()) {
                    heldItem.shrink(count);
                }
            } else {
                launchFireball(player, world, heldItem, level, false, 0);
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
            }
            applyCooldown(player, Items.TNT);
            applyCooldown(player, Items.FIRE_CHARGE);
            event.setCanceled(true);
        }
    }

    private static void launchTnt(Player player, Level world, ItemStack heldItem, int level) {
        Vec3 look = player.getLookAngle();
        PrimedTnt tnt = new PrimedTnt(world,
                player.getX(),
                player.getEyeY(),
                player.getZ(),
                player);

        // 仅潜行连发时添加随机偏移（与火焰弹逻辑一致）
        double randomOffsetX = 0;
        double randomOffsetZ = 0;
        double randomOffsetY = 0;

        if (player.isShiftKeyDown()) {
            randomOffsetX = (world.random.nextDouble() - 0.5) * 0.5;
            randomOffsetZ = (world.random.nextDouble() - 0.5) * 0.5;
            randomOffsetY = (world.random.nextDouble() - 0.5) * 0.5;
        }

        if (look.y < -0.5) {
            Vec3 horizontalLook = new Vec3(look.x, 0, look.z).normalize();
            tnt.setPos(
                    player.getX() + horizontalLook.x,
                    player.getEyeY(),
                    player.getZ() + horizontalLook.z
            );
            randomOffsetY = Math.max(0, randomOffsetY) * 0.3;
        }

        tnt.setDeltaMovement(
                look.x * 1.5 * level + randomOffsetX,
                look.y * 1.5 * level + randomOffsetY,
                look.z * 1.5 * level + randomOffsetZ
        );
        tnt.setFuse(40 - (10 * level));

        world.addFreshEntity(tnt);
    }

    private static void launchFireball(Player player, Level world, ItemStack heldItem, int level, boolean isBurstShot, int shotIndex) {
        Vec3 look = player.getLookAngle();
        double speed = 1.5 * level;

        double spreadFactor = 0.4;
        double randomOffsetX = isBurstShot ? (world.random.nextDouble() - 0.5) * spreadFactor * (shotIndex % 2 == 0 ? 1 : -1) : 0;
        double randomOffsetY = isBurstShot ? (world.random.nextDouble() * 0.2 * (shotIndex % 3)) : 0;
        double randomOffsetZ = isBurstShot ? (world.random.nextDouble() - 0.5) * spreadFactor * (shotIndex % 2 == 1 ? 1 : -1) : 0;

        LargeFireball fireball = new LargeFireball(
                world,
                player,
                look.x * speed + randomOffsetX,
                look.y * speed + randomOffsetY,
                look.z * speed + randomOffsetZ,
                level + 1
        ) {
            @Override
            public boolean isPickable() {
                return false;
            }
        };

        double forwardOffset = 0.5 + (isBurstShot ? shotIndex * 0.1 : 0);
        fireball.setPos(
                player.getX() + look.x * forwardOffset,
                player.getEyeY() + look.y * forwardOffset + (isBurstShot ? shotIndex * 0.05 : 0),
                player.getZ() + look.z * forwardOffset
        );

        world.addFreshEntity(fireball);

        if (!world.isClientSide() && (shotIndex == 0 || !isBurstShot)) {
            world.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GHAST_SHOOT,
                    SoundSource.HOSTILE,
                    isBurstShot ? 1.5F : 1.0F,
                    isBurstShot ? 0.9F - shotIndex * 0.02F : 1.0F
            );
        }
    }

    private static boolean isOnCooldown(Player player, Item item) {
        return player.getCooldowns().isOnCooldown(item);
    }

    private static void applyCooldown(Player player, Item item) {
        player.getCooldowns().addCooldown(item, COOLDOWN_TICKS);
    }
}