package com.playymcmc007.DeepSeeksEnchant.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.playymcmc007.DeepSeeksEnchant.config.ChaosDamageConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ChaosDamageCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("chaosdamage")
                .requires(source -> source.hasPermission(1))
                .then(Commands.literal("messages")
                        .then(Commands.literal("on")
                                .executes(context -> {
                                    ChaosDamageConfig.setShowDamageMessages(true);
                                    context.getSource().sendSuccess(
                                            () -> Component.translatable("command.deepseeksenchant.chaosdamage.messages.on"),
                                            true);
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(Commands.literal("off")
                                .executes(context -> {
                                    ChaosDamageConfig.setShowDamageMessages(false);
                                    context.getSource().sendSuccess(
                                            () -> Component.translatable("command.deepseeksenchant.chaosdamage.messages.off"),
                                            true);
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .executes(context -> {
                            context.getSource().sendSuccess(
                                    () -> Component.translatable("command.deepseeksenchant.chaosdamage.messages.status",
                                            ChaosDamageConfig.shouldShowDamageMessages() ?
                                                    Component.translatable("command.deepseeksenchant.status.on") :
                                                    Component.translatable("command.deepseeksenchant.status.off")),
                                    true);
                            return Command.SINGLE_SUCCESS;
                        }))
        );
    }
}