//package net.levelz.mixin.compat;
//
//import net.levelz.access.LevelManagerAccess;
//import net.levelz.level.LevelManager;
//import net.minecraft.block.AnvilBlock;
//import net.minecraft.block.BlockState;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.Formatting;
//import net.minecraft.util.hit.BlockHitResult;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(value = AnvilBlock.class, priority = 999)
//public class EasyAnvilsAnvilMixin {
//
//    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
//    private void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
//        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//        if (!levelManager.hasRequiredBlockLevel(state.getBlock())) {
//            player.sendMessage(Text.translatable("item.levelz.locked.tooltip").formatted(Formatting.RED), true);
//            info.setReturnValue(ActionResult.FAIL);
//        }
//    }
//
//}
