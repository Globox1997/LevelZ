package net.levelz.mixin.item;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Inject(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void dispenseArmorMixin(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> info, BlockPos blockPos, List<LivingEntity> list, LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity && armor.getItem() instanceof ArmorItem) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (!playerEntity.isCreative()) {
                int playerDefenseLevel = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager(playerEntity).getLevel("defense");
                if (playerDefenseLevel < ConfigInit.CONFIG.maxLevel) {
                    if ((int) (((ArmorItem) armor.getItem()).getMaterial().getEnchantability() / 2.5F) > playerDefenseLevel) {
                        info.setReturnValue(false);
                    }
                }
            }

        }

    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void useMixin(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info, ItemStack itemStack, EquipmentSlot equipmentSlot,
            ItemStack itemStack2) {
        if (!user.isCreative()) {
            int playerDefenseLevel = ((PlayerStatsManagerAccess) user).getPlayerStatsManager(user).getLevel("defense");
            if (playerDefenseLevel < ConfigInit.CONFIG.maxLevel) {
                if ((int) (((ArmorItem) (Object) this).getMaterial().getEnchantability() / 2.5F) > playerDefenseLevel) {
                    info.setReturnValue(TypedActionResult.fail(itemStack));
                }
            }
        }
    }
}
