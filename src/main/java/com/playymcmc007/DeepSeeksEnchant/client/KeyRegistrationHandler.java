package com.playymcmc007.DeepSeeksEnchant.client;

import com.playymcmc007.DeepSeeksEnchant.DeepSeeksEnchant;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeepSeeksEnchant.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyRegistrationHandler {
    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.REGURGITATE_KEY);
    }
}