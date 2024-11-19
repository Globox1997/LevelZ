package net.levelz.mixin.item;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Inject(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void dispenseArmorMixin(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> info, BlockPos blockPos, List<LivingEntity> list, LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (!levelManager.hasRequiredItemLevel(armor.getItem())) {
                info.setReturnValue(false);
            }
        }
    }

}
