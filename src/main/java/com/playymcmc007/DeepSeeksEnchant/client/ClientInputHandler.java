package com.playymcmc007.DeepSeeksEnchant.client;

import com.playymcmc007.DeepSeeksEnchant.DeepSeeksEnchant;
import com.playymcmc007.DeepSeeksEnchant.network.NetworkHandler;
import com.playymcmc007.DeepSeeksEnchant.network.PacketRegurgitate;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeepSeeksEnchant.MOD_ID, value = Dist.CLIENT)
public class ClientInputHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null && mc.level != null) {
            if (KeyBindings.REGURGITATE_KEY.consumeClick()) {
                NetworkHandler.INSTANCE.sendToServer(new PacketRegurgitate());
            }
        }
    }
}