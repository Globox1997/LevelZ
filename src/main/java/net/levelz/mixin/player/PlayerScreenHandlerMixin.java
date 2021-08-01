package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;

@Mixin(value = PlayerScreenHandler.class)
public class PlayerScreenHandlerMixin {

    // @Inject(method = "canInsertIntoSlot(I)Z", at = @At(value = "INVOKE", target =
    // "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), cancellable = true)
    // private void doItemUseMixin(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
    // System.out.println("TEST");
    // }
    // @Inject(method = "canInsertIntoSlot(I)Z", at = @At(value = "INVOKE", target =
    // "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), cancellable = true)
    // private void doItemUseMixin(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
    // System.out.println("TEST");
    // }
    // Lnet/minecraft/screen/PlayerScreenHandler;<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V
    // Lnet/minecraft/screen/PlayerScreenHandler$1;canInsert(Lnet/minecraft/item/ItemStack;)Z

    // @Inject(method = "<init>", at = @At(value = "Lnet/minecraft/entity/LivingEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), cancellable = true)
    // private void canInsertIntoSlotMixin(PlayerScreenHandler playerScreenHandler, ItemStack itemStack, CallbackInfoReturnable<Boolean> info) {
    //     // System.out.println("SLOT" + slot + "::" + stack);
    // }

    // @Inject(method = "Lnet/minecraft/screen/PlayerScreenHandler;canInsertIntoSlot(Lnet/minecraft/item/ItemStack;Lnet/minecraft/screen/slot/Slot;)Z", at = @At(value = "HEAD"), cancellable = true)
    // private void canInsertIntoSlotMixin(ItemStack stack, Slot slot, CallbackInfoReturnable<Boolean> info) {
    // info.setReturnValue(false);
    // }
}
