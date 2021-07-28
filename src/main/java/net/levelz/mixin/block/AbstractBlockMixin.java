package net.levelz.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.LevelJsonInit;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    // public boolean canReplace(ItemPlacementContext context) {
    // return this.getBlock().canReplace(this.asBlockState(), context);
    // }

    // @Inject(method = "canPlaceAt", at = @At(value = "TAIL"))
    // private void canPlaceAtMixin(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
    // System.out.println("test");
    // }

    // @Deprecated
    // public boolean canReplace(BlockState state, ItemPlacementContext context) {
    // return this.material.isReplaceable() && (context.getStack().isEmpty() || !context.getStack().isOf(this.asItem()));
    // }

    // @Inject(method = "calcBlockBreakingDelta", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    // private void calcBlockBreakingDeltaMixin(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info, float f, int i) {
    // System.out.println(f + "::" + i + "::" + player.getBlockBreakingSpeed(state));
    // if (((PlayerStatsManagerAccess) player).getPlayerStatsManager(player).getLevel("mining") < 5) {
    // info.setReturnValue(player.getBlockBreakingSpeed(state) / f / (float) i);
    // }
    // }

    @ModifyVariable(method = "calcBlockBreakingDelta", at = @At(value = "TAIL"), ordinal = 0)
    private int calcBlockBreakingDeltaMixin(int original, BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        int playerMiningLevel = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player).getLevel("mining");
        for (int i = 0; i < LevelJsonInit.MINING_LEVEL_LIST.size(); i++) {

        }
        if (playerMiningLevel < 5) {
            return (int) (original * 0.8F);
        } else
            return original;
    }
    // BlockState state, PlayerEntity player, BlockView world, BlockPos pos,
}