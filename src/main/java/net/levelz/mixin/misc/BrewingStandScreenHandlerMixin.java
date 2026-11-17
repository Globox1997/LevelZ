package net.levelz.mixin.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandMenu.class)
public class BrewingStandScreenHandlerMixin {

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 1, shift = Shift.AFTER), cancellable = true)
    private void transferSlotMixin(Player player, int index, CallbackInfoReturnable<ItemStack> info, @Local(ordinal = 1) ItemStack itemStack2) {
        if (player.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
        if (!levelManager.hasRequiredCraftingLevel(itemStack2.getItem())) {
            info.setReturnValue(ItemStack.EMPTY);
        }
    }
}
