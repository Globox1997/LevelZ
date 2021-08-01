package net.levelz.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;

@Mixin(MiningToolItem.class)
public class MiningToolItemMixin {

    // @Inject(method = "getMiningSpeedMultiplier", at = @At(value = "HEAD"), cancellable = true)
    // private void getMiningSpeedMultiplierMixin(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> info) {
    //     if(){
            
    //     }
    // }

}
