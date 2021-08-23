package net.levelz.mixin.compat;

// Doesn't find refmap in prod env

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Shadow;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// import dev.emi.trinkets.TrinketSlot;
// import dev.emi.trinkets.api.TrinketInventory;

// import org.spongepowered.asm.mixin.injection.At;

// import net.levelz.data.LevelLists;
// import net.levelz.stats.PlayerStatsManager;
// import net.minecraft.entity.player.PlayerEntity;
// import net.minecraft.item.ItemStack;
// import net.minecraft.item.Items;

// @Mixin(TrinketSlot.class)
// public class TrinketSlotMixin {
// @Shadow
// private TrinketInventory trinketInventory;

// @Inject(method = "Ldev/emi/trinkets/TrinketSlot;canInsert(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), remap = false, cancellable = true)
// private void canInsertMixin(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
// if (stack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) trinketInventory.getComponent().getEntity(), LevelLists.elytraList, null, true)) {
// info.setReturnValue(false);
// }
// }
// }
