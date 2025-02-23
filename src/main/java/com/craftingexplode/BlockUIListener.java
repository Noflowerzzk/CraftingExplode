package com.craftingexplode;

import com.craftingexplode.depend.PosWithWorld;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class BlockUIListener {
    // 用于存储玩家打开UI的时间戳
    private static final Map<PosWithWorld, Long> openedBlockTimes = new HashMap<>();
    private static final Map<PlayerEntity, BlockPos> openedPlayerBlocks = new HashMap<>();

    public static void register() {
        // 注册 UseBlock 回调
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            BlockState blockState = world.getBlockState(pos);

            // 检查是否是一个可交互的 UI 方块（例如工作台、箱子、熔炉等）
            if (isInteractiveBlock(blockState)) {
                // 记录打开方块的时间
                if (player instanceof ServerPlayerEntity) {
                    System.out.println(((ServerPlayerEntity) player).getName().getString() + " 打开了可交互UI方块！");
                    openedBlockTimes.put(new PosWithWorld(pos, player.getWorld()), System.currentTimeMillis());
                    openedPlayerBlocks.put((PlayerEntity) player, pos);

                }
            }

            // 返回 ActionResult.PASS，表示允许方块执行其默认的交互行为
            return ActionResult.PASS;
        });

        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // 定义命令 "/mycommand <number>"
            dispatcher.register(
                    CommandManager.literal("setExplodeTime")  // 命令名
                            .then(CommandManager.argument("timeout", LongArgumentType.longArg())  // 添加数字参数
                                    .executes(TimeOutDetector::setTimeoutThreshold)  // 执行时调用的逻辑
                            )
            );
        });

        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // 定义命令 "/mycommand"
            dispatcher.register(
                    CommandManager.literal("setExplodeTime")  // 命令名
                            .executes(TimeOutDetector::showTimeoutThreshold)
            );
        });

        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // 定义命令 "/mycommand <number>"
            dispatcher.register(
                    CommandManager.literal("setExplodeLevel")  // 命令名
                            .then(CommandManager.argument("level", FloatArgumentType.floatArg())  // 添加数字参数
                                    .executes(TimeOutDetector::setExplodeLevel)  // 执行时调用的逻辑
                            )
            );
        });

        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // 定义命令 "/mycommand"
            dispatcher.register(
                    CommandManager.literal("setExplodeLevel")  // 命令名
                            .executes(TimeOutDetector::showExplodeLevel)
            );
        });


//        openInventoryListener();
    }

//    private static void openInventoryListener() {
//        // 获取玩家的当前屏幕处理器
//        ServerTickEvents.END_SERVER_TICK.register(server -> {
//            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
//                if (player.currentScreenHandler == null) {
//                    continue;
//                }
//
//                try {
//                    // 这里使用 try-catch 捕获可能的 UnsupportedOperationException 异常
//                    ScreenHandlerType<?> screenHandlerType = player.currentScreenHandler.getType();
//
//                    if (screenHandlerType == ScreenHandlerType.GENERIC_9X3) {
//                        BlockPos pos = player.getBlockPos();
//                        openedBlockTimes.put(pos, System.currentTimeMillis());
//                        System.out.println(player.getName().getString() + " 打开了物品栏！");
//                    }
//
//                } catch (UnsupportedOperationException e) {
//                    // 捕获到异常时，打印错误信息并继续执行游戏
//                    System.err.println("无法获取 screenHandler 类型，可能是由于无法构造该菜单：" + e.getMessage());
//                }
//            }
//        });
//    }

    // 检查方块是否是可交互的UI方块
    private static boolean isInteractiveBlock(BlockState blockState) {
        // 可交互方块的例子（工作台、箱子、熔炉等）
        return blockState.isOf(Blocks.CRAFTING_TABLE) ||
                blockState.isOf(Blocks.CHEST) ||
                blockState.isOf(Blocks.FURNACE) ||
                blockState.isOf(Blocks.BREWING_STAND) ||
                blockState.isOf(Blocks.SMOKER) ||
                blockState.isOf(Blocks.BLAST_FURNACE) ||
                blockState.isOf(Blocks.STONECUTTER) ||
                blockState.isOf(Blocks.ANVIL) ||
                blockState.isOf(Blocks.SMITHING_TABLE) ||
                blockState.isOf(Blocks.BARREL) ||
                blockState.isOf(Blocks.LOOM) ||
                blockState.isOf(Blocks.GRINDSTONE) ||
                blockState.isOf(Blocks.ENCHANTING_TABLE) ||
                blockState.isOf(Blocks.ENDER_CHEST) ||
                blockState.isOf(Blocks.SHULKER_BOX) ||
                blockState.isOf(Blocks.CARTOGRAPHY_TABLE) ||
                blockState.isOf(Blocks.BREWING_STAND);

    }

//    private static ScreenHandlerType detectScreenHandlerType(BlockState blockState) {
//        if(blockState.isOf(Blocks.CRAFTING_TABLE)) { return ScreenHandlerType.CRAFTING; }
//        if(blockState.isOf(Blocks.FURNACE)) { return ScreenHandlerType.FURNACE; }
//        if(blockState.isOf(Blocks.SMOKER)) { return ScreenHandlerType.SMOKER; }
//        if(blockState.isOf(Blocks.BREWING_STAND)) { return ScreenHandlerType.BREWING_STAND; }
//        if(blockState.isOf(Blocks.SMOKER)) { return ScreenHandlerType.SMOKER; }
//        if(blockState.isOf(Blocks.BLAST_FURNACE)) { return ScreenHandlerType.BLAST_FURNACE; }
//        return null;
//    }

    public static Map<PosWithWorld, Long> getOpenedBlockTimes() {
        return openedBlockTimes;
    }

    public static Map<PlayerEntity, BlockPos> getOpenedPlayerBlocks() {
        return openedPlayerBlocks;
    }
}