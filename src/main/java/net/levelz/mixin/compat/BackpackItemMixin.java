//package net.levelz.mixin.compat;
//
//import draylar.inmis.item.BackpackItem;
//import net.levelz.access.LevelManagerAccess;
//import net.levelz.level.LevelManager;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(BackpackItem.class)
//public class BackpackItemMixin {
//
//    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
//    private static void openScreenMixin(Player player, ItemStack backpackItemStack, CallbackInfo info) {
//        if (player.level() != null && !player.level().isClientSide()) {
//            if (player.isCreative()) {
//                return;
//            }
//            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//            if (!levelManager.hasRequiredStackLevel(backpackItemStack)) {
//                info.cancel();
//            }
//        }
//    }
//}
