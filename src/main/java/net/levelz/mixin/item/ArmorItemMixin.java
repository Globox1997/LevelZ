package net.levelz.mixin.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EquipmentDispenseItemBehavior.class)
public class ArmorItemMixin {

    @Inject(method = "dispenseEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"), cancellable = true)
    private static void dispenseArmorMixin(BlockSource pointer, ItemStack armor, CallbackInfoReturnable<Boolean> info, @Local LivingEntity livingEntity) {
        if (livingEntity instanceof Player playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (!levelManager.hasRequiredStackLevel(armor)) {
                info.setReturnValue(false);
            }
        }
    }

}
