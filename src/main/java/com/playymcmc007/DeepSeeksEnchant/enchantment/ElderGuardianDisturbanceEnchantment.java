package com.playymcmc007.DeepSeeksEnchant.enchantment;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ElderGuardianDisturbanceEnchantment extends Enchantment {

    public ElderGuardianDisturbanceEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMinCost(int level) {
        return 30;
    }

    @Override
    public int getMaxCost(int level) {
        return 50;
    }


    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Mod.EventBusSubscriber(modid = "deepseeksenchant")
    public static class AuraHandler {
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (!EnchantmentToggleConfig.ELDER_GUARDIAN_DISTURBANCE_ENABLED.get()) {
                return;
            }
            if (event.phase == TickEvent.Phase.END) {
                Player player = event.player;
                Level level = player.level();

                if (level.isClientSide &&
                        player.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(ModEnchantments.ELDER_GUARDIAN_DISTURBANCE .get()) > 0) {

                    for (int i = 0; i < 3; i++) {
                        level.addParticle(
                                ParticleTypes.ELDER_GUARDIAN,
                                player.getX(),
                                player.getY() + player.getEyeHeight(),
                                player.getZ(),
                                0, 0, 0
                        );
                    }
                }
            }
        }
    }
}