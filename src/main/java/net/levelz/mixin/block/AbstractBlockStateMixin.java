package net.levelz.mixin.block;

import net.levelz.util.BonusHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    // Set player inventory block breaking calculation here
    @Inject(method = "onBlockBreakStart", at = @At(value = "HEAD"))
    private void onBlockBreakStartMixin(World world, BlockPos pos, PlayerEntity player, CallbackInfo info) {
        BonusHelper.miningEfficiencyBonus(player, world, pos);
    }

}
