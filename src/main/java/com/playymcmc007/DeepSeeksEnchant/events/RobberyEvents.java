package com.playymcmc007.DeepSeeksEnchant.events;

import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import com.playymcmc007.DeepSeeksEnchant.enchantment.RobberyEnchantment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "deepseeksenchant")
public class RobberyEvents {
    @SubscribeEvent
    public static void onVillagerRobbed(LivingDeathEvent event) {
        if (!EnchantmentToggleConfig.ROBBERY_ENABLED.get()) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof Player player) ||
                event.getEntity().level().isClientSide()) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) return;

        LootParams.Builder paramsBuilder = new LootParams.Builder((ServerLevel) player.level())
                .withParameter(LootContextParams.THIS_ENTITY, event.getEntity())
                .withParameter(LootContextParams.ORIGIN, event.getEntity().position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, event.getSource())
                .withParameter(LootContextParams.KILLER_ENTITY, player)
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);

        // 使用正确的LootContextParamSets.ENTITY
        RobberyEnchantment.handleVillagerKill(paramsBuilder.create(LootContextParamSets.ENTITY), weapon);
    }
}