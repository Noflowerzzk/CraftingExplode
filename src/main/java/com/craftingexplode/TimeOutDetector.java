package com.craftingexplode;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class TimeOutDetector {
    private static final long TIMEOUT_THRESHOLD = 3500; // 3秒 = 3000毫秒

    public static void register() {
        // 使用ServerTickEvents替代废弃的ServerTickCallback
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            long currentTime = System.currentTimeMillis();
            for (Map.Entry<BlockPos, Long> entry : BlockUIListener.getOpenedBlockTimes().entrySet()) {
                BlockPos blockPos = entry.getKey();
                long openedTime = entry.getValue();

                // 检查是否已经超过3秒且UI没有关闭
                if (currentTime - openedTime >= TIMEOUT_THRESHOLD) {
//                    ServerWorld world = (ServerWorld) server.getWorld(server.getRegistryKey()); // 获取方块所在的世界
                    triggerExplosion(server.getWorld(World.OVERWORLD), blockPos);
                    // 移除已经爆炸的方块
                    BlockUIListener.getOpenedBlockTimes().remove(blockPos);
                }
            }
        });
    }

    private static void triggerExplosion(World world, BlockPos pos) {
        // 生成爆炸（没有玩家的伤害）
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 6.0F, World.ExplosionSourceType.TNT);
        System.out.println("爆炸在位置 " + pos + " 发生！");
    }
}
