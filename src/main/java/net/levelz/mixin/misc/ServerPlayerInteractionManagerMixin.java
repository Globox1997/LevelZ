package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    // @Inject(method = "tryBreakBlock", at = @At(value = "HEAD"))
    // private void tryBreakBlockMixin(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
    //     System.out.println("T:" + System.nanoTime());
    // }

    // @Inject(method = "tryBreakBlock", at = @At(value = "RETURN"))
    // private void tryBreakBlockMixinSecond(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
    //     System.out.println("U:" + System.nanoTime());
    // }

    // @Inject(method = "processBlockBreakingAction", at = @At(value = "HEAD"))
    // private void processBlockBreakingActionMixin(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo info) {
    //     System.out.println("S:" + System.nanoTime());
    // }

    // @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;tryBreakBlock(Lnet/minecraft/util/math/BlockPos;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    // private void updateMixin(CallbackInfo info, BlockState blockState, float f) {
    //     System.out.println("R:" + System.nanoTime() + " ::" + f);
    // }

    // @Inject(method = "continueMining", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    // private void continueMiningMixin(BlockState state, BlockPos pos, int i, CallbackInfoReturnable<Float> info, int j, float f) {
    //     // System.out.println("Q:" + System.nanoTime() + " ::" + f);
    // }
}
