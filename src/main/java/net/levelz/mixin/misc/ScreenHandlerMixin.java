package net.levelz.mixin.misc;

import net.levelz.util.RestrictionHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class ScreenHandlerMixin {
    @Shadow
    private ItemStack carried;

    @Shadow
    @Final
    @Mutable
    public NonNullList<Slot> slots = NonNullList.create();

    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void internalOnSlotClickMixin(int slotIndex, int button, ContainerInput actionType, Player player, CallbackInfo info) {
        if (player.isCreative()) {
            return;
        }
        if (slotIndex >= 0 && slotIndex != AbstractContainerMenu.SLOT_CLICKED_OUTSIDE && RestrictionHelper.restrictSlotClick(player, actionType, this.carried, this.slots.get(slotIndex), (AbstractContainerMenu) (Object) this)) {
            info.cancel();
        }
    }

}
