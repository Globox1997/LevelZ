package net.levelz.mixin.item;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Equippable.class)
public class EquipmentMixin {

    @Inject(method = "swapWithEquipmentSlot", at = @At("HEAD"), cancellable = true)
    private void equipAndSwapMixin(ItemStack itemStack, Player user, CallbackInfoReturnable<InteractionResult> info) {
        if (user.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) user).getLevelManager();
        if (!levelManager.hasRequiredStackLevel(itemStack)) {
            user.sendOverlayMessage(Component.translatable("item.levelz.locked.tooltip").withStyle(ChatFormatting.RED));
            info.setReturnValue(InteractionResult.FAIL);
        }
    }

}
