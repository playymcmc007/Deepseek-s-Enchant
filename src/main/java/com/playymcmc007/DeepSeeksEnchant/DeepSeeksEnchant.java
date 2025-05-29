package com.playymcmc007.DeepSeeksEnchant;

import com.playymcmc007.DeepSeeksEnchant.command.*;
import com.playymcmc007.DeepSeeksEnchant.config.ChaosDamageConfig;
import com.playymcmc007.DeepSeeksEnchant.effect.ModEffects;
import com.playymcmc007.DeepSeeksEnchant.enchantment.ModEnchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DeepSeeksEnchant.MOD_ID)
public class DeepSeeksEnchant {
    public static final String MOD_ID = "deepseeksenchant";

    public DeepSeeksEnchant() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEnchantments.ENCHANTMENTS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        ChaosDamageConfig.init();
    }
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ChaosDamageCommand.register(event.getDispatcher());
    }
}