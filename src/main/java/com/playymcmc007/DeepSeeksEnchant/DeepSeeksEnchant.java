package com.playymcmc007.DeepSeeksEnchant;

import com.playymcmc007.DeepSeeksEnchant.command.*;
import com.playymcmc007.DeepSeeksEnchant.config.*;
import com.playymcmc007.DeepSeeksEnchant.effect.ModEffects;
import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import com.playymcmc007.DeepSeeksEnchant.enchantment.DeepdarksCallEnchantment;
import com.playymcmc007.DeepSeeksEnchant.events.RegurgitationHandler;
import com.playymcmc007.DeepSeeksEnchant.feature.GiantTreeFeature;
import com.playymcmc007.DeepSeeksEnchant.item.ModItems;
import com.playymcmc007.DeepSeeksEnchant.network.NetworkHandler;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;

@Mod(DeepSeeksEnchant.MOD_ID)
public class DeepSeeksEnchant {
    public static final String MOD_ID = "deepseeksenchant";
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);
    public static final RegistryObject<Feature<?>> GIANT_TREE = FEATURES.register("giant_tree", () -> new GiantTreeFeature(NoneFeatureConfiguration.CODEC));

    public DeepSeeksEnchant() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEnchantments.ENCHANTMENTS.register(modEventBus);
        FEATURES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        ModItems.ITEMS.register(modEventBus);
        NetworkHandler.register();
        EnchantmentToggleConfig.init();
        ChaosDamageConfig.init();
   }
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ChaosDamageCommand.register(event.getDispatcher());
        event.getDispatcher().register(GiantTreeCommand.register());
    }
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            DeepdarksCallEnchantment.onPlayerMove(event.player, event.player.level());
        }
    }
}