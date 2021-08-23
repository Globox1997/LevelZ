package net.levelz.mixin.compat;

// Doesn't find refmap in prod env

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Shadow;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// import dev.emi.trinkets.TrinketSlot;
// import dev.emi.trinkets.api.TrinketInventory;

// import org.spongepowered.asm.mixin.injection.At;

// import net.minecraft.item.ItemStack;

// @Mixin(TrinketSlot.class)
// public class TrinketsPlayerScreenHandlerMixinMixin {
// @Shadow
// private TrinketInventory trinketInventory;

// // @Inject(method = "Ldev/emi/trinkets/TrinketSlot;canInsert(Lnet/minecraft/item/ItemStack;Ldev/emi/trinkets/api/SlotReference;Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), remap =
// // false, cancellable = true)
// // private static void canInsertMixin(ItemStack stack, SlotReference slotRef, LivingEntity entity, CallbackInfoReturnable<Boolean> info) {
// // System.out.println("TEST " + " " + "::" + entity + "::" + stack);
// // info.setReturnValue(false);
// // }

// @Inject(method = "Ldev/emi/trinkets/TrinketSlot;canInsert(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), remap = false, cancellable = true)
// private void canInsertMixin(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
// System.out.println("TEST " + " " + "::" + trinketInventory.getComponent().getEntity() + "::" + stack);
// info.setReturnValue(false);
// }
// }
