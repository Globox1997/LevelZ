package net.levelz.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(OreBlock.class)
public class OreBlockMixin {

    @Inject(method = "onStacksDropped", at = @At(value = "HEAD"), cancellable = true)
    private void onStacksDroppedMixin(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, CallbackInfo info) {
        // info.cancel();
        // Maybe check for closest player?
    }
}