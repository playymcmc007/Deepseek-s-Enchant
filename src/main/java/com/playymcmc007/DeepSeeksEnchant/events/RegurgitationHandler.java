package com.playymcmc007.DeepSeeksEnchant.events;

import com.playymcmc007.DeepSeeksEnchant.DeepSeeksEnchant;
import com.playymcmc007.DeepSeeksEnchant.config.EnchantmentToggleConfig;
import com.playymcmc007.DeepSeeksEnchant.item.ModItems;
import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RegurgitationHandler {
    private static final SoundEvent REGURGITATE_SOUND =
            SoundEvent.createVariableRangeEvent(
                    new ResourceLocation(DeepSeeksEnchant.MOD_ID, "regurgitate")
            );

    private static final String FOOD_HISTORY_KEY = "RegurgitationFoodHistory";
    private static final int BASE_STORAGE = 3;

    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!EnchantmentToggleConfig.REGURGITATION_ENABLED.get()) {
            return;
        }
        if (event.getEntity() instanceof Player player &&
                event.getItem().isEdible() &&
                !event.getItem().getItem().equals(ModItems.REGURGITATED_FOOD.get())) {

            ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
            int enchantLevel = chestArmor.getEnchantmentLevel(ModEnchantments.REGURGITATION.get());
            if (enchantLevel > 0) {
                int maxStorage = BASE_STORAGE + (enchantLevel - 1);
                CompoundTag tag = chestArmor.getOrCreateTag();
                ListTag history = tag.getList(FOOD_HISTORY_KEY, 10);

                if (history.size() < maxStorage) {
                    CompoundTag foodTag = new CompoundTag();
                    event.getItem().save(foodTag);
                    history.add(foodTag);
                    tag.put(FOOD_HISTORY_KEY, history);
                    chestArmor.setTag(tag);
                }
            }
        }
    }

    public static void regurgitate(Player player) {
        if (!EnchantmentToggleConfig.REGURGITATION_ENABLED.get()) {
            return;
        }
        Level level = player.level();
        ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestArmor.isEmpty()) return;  // 确保胸甲存在

        CompoundTag tag = chestArmor.getTag();
        if (tag == null || !tag.contains(FOOD_HISTORY_KEY)) return;

        ListTag history = tag.getList(FOOD_HISTORY_KEY, 10);
        if (history.isEmpty()) return;

        // 播放音效
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                REGURGITATE_SOUND,
                SoundSource.PLAYERS,
                1.0F,
                0.8F + player.getRandom().nextFloat() * 0.4F
        );

        // 获取最新食物数据
        CompoundTag foodTag = history.getCompound(history.size() - 1);
        ItemStack originalFood = ItemStack.of(foodTag);
        FoodProperties foodProperties = originalFood.getFoodProperties(player);
        if (foodProperties == null) return;

        // 扣除饱食度和饱和度
        deductFoodStats(player, foodProperties);

        // 创建反刍物品
        ItemStack regurgitatedFood = createRegurgitatedFood(foodTag);

        // 生成掉落物
        spawnRegurgitationEffects(player, regurgitatedFood);

        // 更新食物历史
        if (!history.isEmpty()) {
            history.remove(history.size() - 1);
        }
        tag.put(FOOD_HISTORY_KEY, history);
        chestArmor.setTag(tag);
    }

    private static void deductFoodStats(Player player, FoodProperties foodProperties) {
        FoodData foodData = player.getFoodData();
        int nutrition = foodProperties.getNutrition();
        float saturation = foodProperties.getSaturationModifier();

        // 扣除饱和度
        float currentSaturation = foodData.getSaturationLevel();
        if (currentSaturation >= saturation) {
            foodData.setSaturation(currentSaturation - saturation);
        } else {
            float remaining = saturation - currentSaturation;
            foodData.setSaturation(0);
            foodData.setFoodLevel(foodData.getFoodLevel() - (int) Math.ceil(remaining));
        }

        // 扣除饱食度
        int currentFoodLevel = foodData.getFoodLevel();
        if (currentFoodLevel >= nutrition) {
            foodData.setFoodLevel(currentFoodLevel - nutrition);
        } else {
            int remaining = nutrition - currentFoodLevel;
            foodData.setFoodLevel(0);
            player.hurt(player.damageSources().starve(), remaining);
        }
    }

    private static ItemStack createRegurgitatedFood(CompoundTag foodTag) {
        ItemStack regurgitatedFood = new ItemStack(ModItems.REGURGITATED_FOOD.get());
        CompoundTag regTag = new CompoundTag();
        regTag.put("OriginalFood", foodTag);
        regurgitatedFood.setTag(regTag);
        return regurgitatedFood;
    }

    private static ItemStack getOriginalFood(ItemStack regurgitatedFood) {
        if (regurgitatedFood.getTag() != null && regurgitatedFood.getTag().contains("OriginalFood")) {
            return ItemStack.of(regurgitatedFood.getTag().getCompound("OriginalFood"));
        }
        return ItemStack.EMPTY;
    }
    private static void spawnRegurgitationEffects(Player player, ItemStack regurgitatedFood) {
        Level level = player.level();
        if (level.isClientSide()) return;

        Vec3 lookAngle = player.getLookAngle();
        Vec3 mouthPos = new Vec3(
                player.getX(),
                player.getEyeY() - 0.3,
                player.getZ()
        );

        // 生成掉落物
        ItemEntity entity = new ItemEntity(
                level,
                mouthPos.x,
                mouthPos.y,
                mouthPos.z,
                regurgitatedFood
        );
        entity.setPickUpDelay(40);
        entity.setDeltaMovement(
                lookAngle.x * 0.2,
                lookAngle.y * 0.2 + 0.1,
                lookAngle.z * 0.2
        );
        level.addFreshEntity(entity);

        // 添加粒子效果 (需要在客户端同步)
        if (level instanceof ServerLevel serverLevel) {
            ItemStack originalFood = getOriginalFood(regurgitatedFood);
            if (originalFood.isEmpty()) {
                originalFood = regurgitatedFood; // 回退使用反刍食物本身
            }
            for (int i = 0; i < 20; i++) {
                serverLevel.sendParticles(
                        new ItemParticleOption(ParticleTypes.ITEM, originalFood),
                        mouthPos.x,
                        mouthPos.y,
                        mouthPos.z,
                        5, // 每组5个粒子
                        0.2, // x扩散范围
                        0.1, // y扩散范围
                        0.2, // z扩散范围
                        0 // 基础速度
                );


                serverLevel.sendParticles(
                        new ItemParticleOption(ParticleTypes.ITEM, regurgitatedFood),
                        mouthPos.x,
                        mouthPos.y,
                        mouthPos.z,
                        5, // 每组5个粒子
                        0.2, // x扩散范围
                        0.1, // y扩散范围
                        0.2, // z扩散范围
                        0 // 基础速度
                );
            }
        }
    }
}