package com.playymcmc007.DeepSeeksEnchant.item;

import com.playymcmc007.DeepSeeksEnchant.DeepSeeksEnchant;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = DeepSeeksEnchant.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DeepSeeksEnchant.MOD_ID);

    public static final RegistryObject<Item> REGURGITATED_FOOD = ITEMS.register("regurgitated_food",
            () -> new RegurgitatedFoodItem(new Item.Properties()
                    .food(new FoodProperties.Builder() // 必须保留基础FoodProperties
                            .nutrition(0) // 设为0，实际值由动态逻辑覆盖
                            .saturationMod(0)
                            .alwaysEat() // 允许在任何饱食度下食用
                            .build())
                    .stacksTo(16)
            ));

    public static final RegistryObject<Item> GENERATE_DECEPTION = ITEMS.register("generate_deception",
            () -> new GenerateDeceptionItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    @SubscribeEvent
    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(REGURGITATED_FOOD);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.GENERATE_DECEPTION.get());
        }
    }

}