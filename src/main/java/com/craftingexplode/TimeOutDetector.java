package com.craftingexplode;

import com.craftingexplode.depend.PosWithWorld;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeOutDetector {
    private static long TIMEOUT_THRESHOLD = 3500; // 3秒 = 3000毫秒
    private static Float EXPLODE_LEVEL = 6.0f;

    public static void register() {
        // 使用ServerTickEvents替代废弃的ServerTickCallback
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            long currentTime = System.currentTimeMillis();
            List<PosWithWorld> toRemove = new ArrayList<PosWithWorld>();
            for (Map.Entry<PosWithWorld, Long> entry : BlockUIListener.getOpenedBlockTimes().entrySet()) {
                BlockPos blockPos = entry.getKey().getPos();
                World world = entry.getKey().getWorld();
                long openedTime = entry.getValue();

                // 检查是否已经超过3秒且UI没有关闭
                if (currentTime - openedTime >= TIMEOUT_THRESHOLD) {
//                    ServerWorld world = (ServerWorld) server.getWorld(server.getRegistryKey()); // 获取方块所在的世界
                    triggerExplosion(world, blockPos);
                    // 移除已经爆炸的方块
//                    BlockUIListener.getOpenedBlockTimes().remove(entry.getKey());
                    toRemove.add(entry.getKey());
                }
            }

            for (PosWithWorld posWithWorld : toRemove) {
                BlockUIListener.getOpenedBlockTimes().remove(posWithWorld);
            }
        });
    }

    private static void triggerExplosion(World world, BlockPos pos) {
        // 生成爆炸（没有玩家的伤害）
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), EXPLODE_LEVEL, World.ExplosionSourceType.TNT);
        System.out.println("爆炸在位置 " + pos + " 发生！");
    }

    public static int showTimeoutThreshold(CommandContext<ServerCommandSource> context) {
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("当前爆炸时间为 " + TIMEOUT_THRESHOLD + " 毫秒"), false);
        return 0;
    }

    public static int setTimeoutThreshold(CommandContext<ServerCommandSource> context) {
        Long temp = LongArgumentType.getLong(context, "timeout");
        if (temp <= 0) {
            context.getSource().getServer().getPlayerManager().broadcast(Text.literal("时间需大于 0！"), false);
            return 1;
        }
        TIMEOUT_THRESHOLD = temp;
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("爆炸时间已设置为 " + TIMEOUT_THRESHOLD + " 毫秒"), false);
        return 0;
    }

    public static int showExplodeLevel(CommandContext<ServerCommandSource> context) {
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("当前爆炸等级为 " + EXPLODE_LEVEL), false);
        return 0;
    }

    public static int setExplodeLevel(CommandContext<ServerCommandSource> context) {
        Float temp = FloatArgumentType.getFloat(context, "level");
        if (temp <= 0) {
            context.getSource().getServer().getPlayerManager().broadcast(Text.literal("爆炸等级需大于 0！"), false);
            return 1;
        }
        EXPLODE_LEVEL = temp;
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("爆炸等级已设置为 " + EXPLODE_LEVEL), false);
        return 0;
    }
}
