package com.playymcmc007.DeepSeeksEnchant.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.playymcmc007.DeepSeeksEnchant.DeepSeeksEnchant;
import com.playymcmc007.DeepSeeksEnchant.feature.GiantTreeFeature;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GiantTreeCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("gianttree")
                .requires(source -> source.hasPermission(2))
                // 帮助命令 - 单独的子命令
                .then(Commands.literal("help")
                        .executes(context -> {
                            sendHelp(context.getSource());
                            return Command.SINGLE_SUCCESS;
                        })
                )
                // 生成命令 - 单独的子命令
                .then(Commands.literal("generate")
                        .executes(context -> {
                            BlockPos pos = BlockPos.containing(context.getSource().getPosition());
                            return generateCustomTree(context.getSource(), pos,
                                    new ResourceLocation("minecraft", "oak_log"),
                                    new ResourceLocation("minecraft", "oak_leaves"),
                                    100);
                        })
                        .then(Commands.argument("trunkBlock", ResourceLocationArgument.id())
                                .then(Commands.argument("foliageBlock", ResourceLocationArgument.id())
                                        .executes(context -> {
                                            BlockPos pos = BlockPos.containing(context.getSource().getPosition());
                                            ResourceLocation trunkLoc = ResourceLocationArgument.getId(context, "trunkBlock");
                                            ResourceLocation foliageLoc = ResourceLocationArgument.getId(context, "foliageBlock");
                                            return generateCustomTree(context.getSource(), pos, trunkLoc, foliageLoc, 100);
                                        })
                                        .then(Commands.argument("height", IntegerArgumentType.integer(10, 500))
                                                .executes(context -> {
                                                    BlockPos pos = BlockPos.containing(context.getSource().getPosition());
                                                    ResourceLocation trunkLoc = ResourceLocationArgument.getId(context, "trunkBlock");
                                                    ResourceLocation foliageLoc = ResourceLocationArgument.getId(context, "foliageBlock");
                                                    int height = IntegerArgumentType.getInteger(context, "height");
                                                    return generateCustomTree(context.getSource(), pos, trunkLoc, foliageLoc, height);
                                                })
                                        )
                                )
                        )
                        // 默认执行帮助
                        .executes(context -> {
                            sendHelp(context.getSource());
                            return Command.SINGLE_SUCCESS;
                        }));
    }

    private static int generateCustomTree(CommandSourceStack source, BlockPos pos,
                                          ResourceLocation trunkLoc, ResourceLocation foliageLoc,
                                          int baseHeight) {
        // 修改为兼容Java 17的写法，同时保留level变量
        if (!(source.getLevel() instanceof ServerLevel)) {
            source.sendFailure(Component.translatable("command.deepseeksenchant.gianttree.error.server_only"));
            return 0;
        }
        ServerLevel level = (ServerLevel) source.getLevel();

        try {
            // 获取树干方块
            if (!BuiltInRegistries.BLOCK.containsKey(trunkLoc)) {
                source.sendFailure(Component.translatable("command.deepseeksenchant.gianttree.error.invalid_trunk", trunkLoc));
                return 0;
            }
            Block trunkBlock = BuiltInRegistries.BLOCK.get(trunkLoc);

            // 获取树叶方块
            if (!BuiltInRegistries.BLOCK.containsKey(foliageLoc)) {
                source.sendFailure(Component.translatable("command.deepseeksenchant.gianttree.error.invalid_foliage", foliageLoc));
                return 0;
            }
            Block foliageBlock = BuiltInRegistries.BLOCK.get(foliageLoc);

            source.sendSuccess(() ->
                            Component.translatable("command.deepseeksenchant.gianttree.generating",
                                    baseHeight, trunkLoc, foliageLoc),
                    false
            );

            // 生成树木
            GiantTreeFeature feature = (GiantTreeFeature) DeepSeeksEnchant.GIANT_TREE.get();
            feature.generateWithMaterials(
                    level,
                    pos,
                    trunkBlock.defaultBlockState(),
                    foliageBlock.defaultBlockState(),
                    level.getRandom()
            );
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            source.sendFailure(Component.translatable("command.deepseeksenchant.gianttree.error.generic", e.getMessage()));
            return 0;
        }
    }

    private static void sendHelp(CommandSourceStack source) {
        source.sendSuccess(() ->
                        Component.translatable("command.deepseeksenchant.gianttree.help")
                                .append("\n").append(Component.translatable("command.deepseeksenchant.gianttree.help.line1"))
                                .append("\n").append(Component.translatable("command.deepseeksenchant.gianttree.help.line2"))
                                .append("\n").append(Component.translatable("command.deepseeksenchant.gianttree.help.line3"))
                                .append("\n").append(Component.translatable("command.deepseeksenchant.gianttree.help.line4"))
                                .append("\n\n").append(Component.translatable("command.deepseeksenchant.gianttree.help.examples"))
                                .append("\n").append(Component.translatable("command.deepseeksenchant.gianttree.help.example1"))
                                .append("\n").append(Component.translatable("command.deepseeksenchant.gianttree.help.example2"))
                                .append("\n").append(Component.translatable("command.deepseeksenchant.gianttree.help.example3"))
                                .append("\n").append(Component.translatable("command.deepseeksenchant.gianttree.help.note")),
                false
        );
    }
}