package net.levelz.mixin.block;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.AbstractBlock;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    // Entry point for breaking block and check unlock list could be here

    // @Inject(method = "calcBlockBreakingDelta", at = @At(value = "HEAD"))
    // private void calcBlockBreakingDeltaTEST(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
    // // System.out.println("A:" + System.nanoTime());
    // }
}
