package com.playymcmc007.DeepSeeksEnchant.events;

import com.playymcmc007.DeepSeeksEnchant.DeepSeeksEnchant;
import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import com.playymcmc007.DeepSeeksEnchant.feature.GiantTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class TreeEnchantmentHandler {
    private static final int BONE_MEAL_COST = 64;

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!EnchantmentToggleConfig.GIANT_GROWTH_ENABLED.get()) {
            return;
        }
        Player player = event.getEntity();
        ItemStack shears = player.getMainHandItem();

        if (shears.getEnchantmentLevel(ModEnchantments.GIANT_GROWTH.get()) <= 0) {
            return;
        }

        BlockState targetState = event.getLevel().getBlockState(event.getPos());
        if (!GiantTreeFeature.LOG_MAP.containsKey(targetState.getBlock())) {
            return;
        }

        int boneMealCount = player.getInventory().countItem(Items.BONE_MEAL);

        if (boneMealCount < BONE_MEAL_COST) {
            player.displayClientMessage(
                    Component.translatable("message.deepseeksenchant.bone_meal.tip"),
                    true
            );
            event.setCanceled(true);
            return;
        }

        int remaining = BONE_MEAL_COST;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == Items.BONE_MEAL) {
                int consume = Math.min(remaining, stack.getCount());
                stack.shrink(consume);
                remaining -= consume;
                if (remaining <= 0) break;
            }
        }

        if (!event.getLevel().isClientSide()) {
            ServerLevel level = (ServerLevel) event.getLevel();
            BlockPos pos = event.getPos();

            BlockState log = GiantTreeFeature.LOG_MAP.get(targetState.getBlock());
            BlockState leaves = GiantTreeFeature.LEAVES_MAP.get(targetState.getBlock());

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            GiantTreeFeature feature = (GiantTreeFeature) DeepSeeksEnchant.GIANT_TREE.get();
            feature.generateWithMaterials(level, pos, log, leaves, level.getRandom());

            if (!player.isCreative()) {
                EnchantmentHelper.setEnchantments(
                        EnchantmentHelper.getEnchantments(shears).entrySet().stream()
                                .filter(e -> e.getKey() != ModEnchantments.GIANT_GROWTH.get())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                        shears
                );
            }
        }
        player.addEffect(new MobEffectInstance(
                MobEffects.NIGHT_VISION,
                72000,
                1,
                false,
                true,
                true
        ));
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}