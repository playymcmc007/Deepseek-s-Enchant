package com.playymcmc007.DeepSeeksEnchant;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments.*;

@Mod.EventBusSubscriber(modid = "deepseeksenchant", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootTableModifier {
    private static final ResourceLocation END_CITY_LOOT =
            new ResourceLocation("minecraft", "chests/end_city_treasure");
    private static final ResourceLocation STRONGHOLD_LIBRARY =
            new ResourceLocation("minecraft", "chests/stronghold_library");
    private static final ResourceLocation ANCIENT_CITY_LOOT =
            new ResourceLocation("minecraft", "chests/ancient_city");
    @SubscribeEvent
    //权重是什么？能吃吗？
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(STRONGHOLD_LIBRARY)) {
            LootPool modBlessingPool = LootPool.lootPool()
                    .name("mod_blessing_pool")
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                            .apply(EnchantRandomlyFunction.randomEnchantment()
                                    .withEnchantment(MODBLESSING.get())
                            )
                    )
                    .build();
            LootPool advancementsBlessingPool = LootPool.lootPool()
                    .name("advancements_blessing_pool")
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                            .apply(EnchantRandomlyFunction.randomEnchantment()
                                    .withEnchantment(ADVBLESSING.get())
                            )
                    )
                    .build();
            LootPool timeBlessingPool = LootPool.lootPool()
                    .name("time_blessing_pool")
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                            .apply(EnchantRandomlyFunction.randomEnchantment()
                                    .withEnchantment(TIMEBLESSING.get())
                            )
                    )
                    .build();
            event.getTable().addPool(modBlessingPool);
            event.getTable().addPool(advancementsBlessingPool);
            event.getTable().addPool(timeBlessingPool);
        }
        if (event.getName().equals(END_CITY_LOOT)) {
            LootPool SplitPool = LootPool.lootPool()
                    .name("SplitPool")
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.2f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                            .apply(EnchantRandomlyFunction.randomEnchantment()
                                    .withEnchantment(SPLIT.get())
                            )
                    )
                    .build();

            LootPool SnipePool = LootPool.lootPool()
                    .name("SnipePool")
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.2f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                            .apply(EnchantRandomlyFunction.randomEnchantment()
                                    .withEnchantment(SNIPE.get())
                            )
                    )
                    .build();

            event.getTable().addPool(SplitPool);
            event.getTable().addPool(SnipePool);
        }
        if (event.getName().equals(ANCIENT_CITY_LOOT)) {
            LootPool DeepdarkPool = LootPool.lootPool()
                    .name("Deepdark_pool")
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.5f))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                            .apply(EnchantRandomlyFunction.randomEnchantment()
                                    .withEnchantment(DEEPDARKSCALL.get())
                            )
                    )
                    .build();
            event.getTable().addPool(DeepdarkPool);
        }
    }
}