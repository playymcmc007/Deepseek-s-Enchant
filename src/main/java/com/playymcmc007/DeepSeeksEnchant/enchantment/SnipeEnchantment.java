package com.playymcmc007.DeepSeeksEnchant.enchantment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber
public class SnipeEnchantment extends Enchantment {
    private static final int KILLER_HIT = 6;
    private static final float MEGA_DAMAGE = 1000000.0F;
    private static final float BASE_COOLDOWN = 3.0F;
    private static final Map<ItemStack, Integer> ITEM_COOLDOWNS = Collections.synchronizedMap(new WeakHashMap<>());
    public SnipeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMinCost(int level) {
        return 30;
    }
    @Override
    public int getMaxCost(int level) {
        return 35;
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
    protected boolean checkCompatibility(Enchantment other) {
        ResourceLocation otherId = ForgeRegistries.ENCHANTMENTS.getKey(other);
        if (otherId.equals(new ResourceLocation("deepseeksenchant", "chaos_damage"))) {
            return false;
        }
        if (other instanceof DamageEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }
        @SubscribeEvent
        public static void onAttack(AttackEntityEvent event) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }
            Player player = (Player) event.getEntity();

            if (!(event.getTarget() instanceof LivingEntity target)) return;

            ItemStack weapon = player.getMainHandItem();
            if (weapon.getEnchantmentLevel(ModEnchantments.SNIPE.get()) <= 0) return;
            CompoundTag tag = weapon.getOrCreateTag();
            long gameTime = player.level().getGameTime();
            if (tag.contains("CooldownEndTick") && gameTime < tag.getLong("CooldownEndTick")) {
                event.setCanceled(true);
                return;
            }

            int hits = tag.getInt("SnipeHits");
            UUID lastTarget = tag.hasUUID("LastTarget") ? tag.getUUID("LastTarget") : null;

            if (!target.getUUID().equals(lastTarget)) {
                hits = 0;
            }
            tag.putUUID("LastTarget", target.getUUID());

            if (++hits >= KILLER_HIT) {
                target.hurt(target.damageSources().playerAttack(player), MEGA_DAMAGE);
                hits = 0;
                player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                for (int i = 0; i < 8; i++) {
                    double xSpeed = (player.level().random.nextDouble() * 1.8) + 0.2;
                    double zSpeed = (player.level().random.nextDouble() * 1.8) + 0.2;
                    player.level().addParticle(
                            ParticleTypes.SONIC_BOOM,
                            target.getX(), target.getY() + target.getBbHeight()/2, target.getZ(),
                            xSpeed, 0.2, zSpeed
                    );
                }
            }
            tag.putInt("SnipeHits", hits);

            float cooldownSeconds = Math.max(1.0F, (target.getHealth() / 10) * BASE_COOLDOWN);
            int cooldownTicks = (int)(cooldownSeconds * 20);
            tag.putLong("CooldownEndTick", gameTime + cooldownTicks);
            tag.putInt("CooldownTicks", cooldownTicks);
        }
    }