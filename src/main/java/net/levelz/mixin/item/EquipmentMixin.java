package net.levelz.mixin.item;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Equipable.class)
public interface EquipmentMixin {

    @Inject(method = "swapWithEquipmentSlot", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    default void equipAndSwapMixin(Item item, Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        if (user.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) user).getLevelManager();
        if (!levelManager.hasRequiredStackLevel(itemStack)) {
            user.displayClientMessage(Component.translatable("item.levelz.locked.tooltip").withStyle(ChatFormatting.RED), true);
            info.setReturnValue(InteractionResultHolder.fail(itemStack));
        }
    }

}
