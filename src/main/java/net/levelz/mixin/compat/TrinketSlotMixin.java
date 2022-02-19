// package net.levelz.mixin.compat;

// import java.util.ArrayList;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
// import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// import dev.emi.trinkets.TrinketSlot;
// import dev.emi.trinkets.api.SlotReference;
// import dev.emi.trinkets.api.TrinketsApi;

// import org.spongepowered.asm.mixin.injection.At;

// import net.levelz.data.LevelLists;
// import net.levelz.stats.PlayerStatsManager;
// import net.minecraft.entity.LivingEntity;
// import net.minecraft.entity.player.PlayerEntity;
// import net.minecraft.item.ItemStack;
// import net.minecraft.text.TranslatableText;
// import net.minecraft.util.Formatting;
// import net.minecraft.util.registry.Registry;

// @Mixin(TrinketSlot.class)
// public class TrinketSlotMixin {

//     // tested with artifality:floral_staff item
//     @Inject(method = "canInsert", at = @At(value = "INVOKE", target = "Ldev/emi/trinkets/api/TrinketsApi;getTrinket(Lnet/minecraft/item/Item;)Ldev/emi/trinkets/api/Trinket;"), locals = LocalCapture.PRINT, cancellable = true)
//     private static void canInsertMixin(ItemStack stack, SlotReference slotRef, LivingEntity entity, CallbackInfoReturnable<Boolean> info, boolean res) {
//         if (res && entity instanceof PlayerEntity && TrinketsApi.getTrinket(stack.getItem()).canEquip(stack, slotRef, entity)) {
//             ArrayList<Object> customList = LevelLists.customItemList;
//             String string = Registry.ITEM.getId(stack.getItem()).toString();
//             if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) entity, customList, string, true)) {
//                 ((PlayerEntity) entity).sendMessage(
//                         new TranslatableText("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2)).formatted(Formatting.RED),
//                         true);
//                 info.setReturnValue(false);
//             }
//         }
//     }
// }
