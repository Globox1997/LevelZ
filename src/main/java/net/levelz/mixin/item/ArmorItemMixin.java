package net.levelz.mixin.item;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Inject(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void dispenseArmorMixin(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> info, BlockPos blockPos, List<LivingEntity> list, LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity && armor.getItem() instanceof ArmorItem) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            try {
                if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(armor.getItem()).toString())) {
                    if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) livingEntity, levelList, Registries.ITEM.getId(armor.getItem()).toString(), true))
                        info.setReturnValue(false);
                } else {
                    levelList = LevelLists.armorList;
                    if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) livingEntity, levelList, ((ArmorItem) armor.getItem()).getMaterial().getName().toLowerCase(), true))
                        info.setReturnValue(false);
                }
            } catch (AbstractMethodError ignore) {
            }
        }
    }

}
