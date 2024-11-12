package net.levelz.mixin.player;

import net.levelz.access.PlayerBreakBlockAccess;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements PlayerBreakBlockAccess {

    @Shadow
    @Mutable
    @Final
    public PlayerEntity player;

    @Unique
    private boolean canBreakBlock = true;
    @Unique
    private float blockBreakExtraDelta = 1.0F;

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "HEAD"), cancellable = true)
    private void getBlockBreakingSpeedMixin(BlockState block, CallbackInfoReturnable<Float> info) {
        if (!this.canBreakBlock) {
            info.setReturnValue(1.0F);
        }
    }

    @Override
    public void setInventoryBlockBreakable(boolean breakable) {
        this.canBreakBlock = breakable;
    }

    @Override
    public void setAbstractBlockBreakDelta(float breakingDelta) {
        this.blockBreakExtraDelta = breakingDelta;
    }

    @Override
    public float getBreakingAbstractBlockDelta() {
        return this.blockBreakExtraDelta;
    }

}
