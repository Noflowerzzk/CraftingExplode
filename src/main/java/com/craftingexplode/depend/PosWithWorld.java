package com.craftingexplode.depend;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class PosWithWorld {

    private final BlockPos pos;

    private final World world;

    public PosWithWorld(BlockPos pos, World world) {
        this.pos = pos;
        this.world = world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // 同一对象
        if (obj == null || getClass() != obj.getClass()) return false; // 判断是否同类

        PosWithWorld that = (PosWithWorld) obj;
        return Objects.equals(pos, that.pos) && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, world); // 基于 pos 和 world 生成哈希值
    }
}
