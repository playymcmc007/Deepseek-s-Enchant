package com.playymcmc007.DeepSeeksEnchant.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RegurgitatedFoodItem extends Item {
    public RegurgitatedFoodItem(Properties properties) {
        super(properties);
    }

    private static final FoodProperties DEFAULT_FOOD = new FoodProperties.Builder()
            .nutrition(0)
            .saturationMod(0)
            .alwaysEat()
            .build();

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("OriginalFood")) {
            ItemStack originalFood = ItemStack.of(tag.getCompound("OriginalFood"));
            FoodProperties originalProps = originalFood.getFoodProperties(entity);
            if (originalProps != null) {
                return new FoodProperties.Builder()
                        .nutrition(Math.max(0, originalProps.getNutrition() / 2))
                        .saturationMod(Math.max(0, originalProps.getSaturationModifier() / 2))
                        .alwaysEat()
                        .build();
            }
        }
        return DEFAULT_FOOD;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("OriginalFood")) {
            ItemStack originalFood = ItemStack.of(tag.getCompound("OriginalFood"));
            tooltip.add(Component.translatable("tooltip.deepseeksenchant.regurgitated_food.source",
                    originalFood.getHoverName()));

        } else {
            tooltip.add(Component.translatable("tooltip.deepseeksenchant.regurgitated_food.invalid"));
        }
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return super.finishUsingItem(stack, level, entity);
    }
}