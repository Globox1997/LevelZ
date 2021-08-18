package net.levelz.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    @Inject(method = "calcBlockBreakingDelta", at = @At(value = "HEAD"))
    private void calcBlockBreakingDeltaTEST(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
        // System.out.println("A:" + System.nanoTime());
    }
}
