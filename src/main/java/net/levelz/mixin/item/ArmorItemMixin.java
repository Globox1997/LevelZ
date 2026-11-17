package net.levelz.mixin.item;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Inject(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void dispenseArmorMixin(BlockSource pointer, ItemStack armor, CallbackInfoReturnable<Boolean> info, BlockPos blockPos, List<LivingEntity> list, LivingEntity livingEntity) {
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
