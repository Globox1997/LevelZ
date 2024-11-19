package net.levelz.mixin.misc;

import net.levelz.util.RestrictionHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Shadow
    private ItemStack cursorStack;

    @Shadow
    @Final
    @Mutable
    public DefaultedList<Slot> slots = DefaultedList.of();

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void internalOnSlotClickMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        if (player.isCreative()) {
            return;
        }
        if (slotIndex >= 0 && slotIndex != ScreenHandler.EMPTY_SPACE_SLOT_INDEX && RestrictionHelper.restrictSlotClick(player, actionType, this.cursorStack, this.slots.get(slotIndex), (ScreenHandler) (Object) this)) {
            info.cancel();
        }
    }

}
