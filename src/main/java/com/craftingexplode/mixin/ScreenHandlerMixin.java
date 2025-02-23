package com.craftingexplode.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.craftingexplode.BlockUIListener;
import com.craftingexplode.depend.PosWithWorld;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
	@Shadow public abstract ScreenHandlerType<?> getType();

	@Shadow @Final @Nullable private ScreenHandlerType<?> type;

	@Inject(method = "onClosed", at = @At("HEAD"))
	private void onScreenClosed(PlayerEntity player, CallbackInfo ci) {
		if (player instanceof ServerPlayerEntity) {
			BlockPos pos = getBlockPos(player);
			BlockUIListener.getOpenedBlockTimes().remove(new PosWithWorld(pos, player.getWorld()));
			System.out.println(player.getName().getString() + " 关闭了可交互UIIIIII方块！");
		}
	}

	@Inject(method = "getType", at = @At("HEAD"))
	private void getTypeMixin(CallbackInfoReturnable<ScreenHandlerType<?>> cir) {
		if (this.type == null) {
			return;
		}
	}

	// 获取玩家正在操作的方块位置（根据实际的交互逻辑进行实现）
	@Unique
	private BlockPos getBlockPos(PlayerEntity player) {
		// 你需要根据实际情况获取当前玩家正在交互的方块位置
		// 以下是一个示例，你可以根据实际情况进行修改
		return BlockUIListener.getOpenedPlayerBlocks().get(player);
	}}