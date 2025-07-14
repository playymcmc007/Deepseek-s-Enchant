package com.playymcmc007.DeepSeeksEnchant.network;

import com.playymcmc007.DeepSeeksEnchant.server.ServerEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRegurgitate {
    public PacketRegurgitate() {}

    public PacketRegurgitate(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // 在服务端执行
            ServerEvents.handleRegurgitation(ctx.getSender());
        });
        return true;
    }
}