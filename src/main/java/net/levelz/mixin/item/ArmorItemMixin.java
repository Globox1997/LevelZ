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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Inject(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void dispenseArmorMixin(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> info, BlockPos blockPos, List<LivingEntity> list, LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity && armor.getItem() instanceof ArmorItem) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            try {
                if (!levelList.isEmpty() && levelList.contains(Registry.ITEM.getId(armor.getItem()).toString())) {
                    if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) livingEntity, levelList, Registry.ITEM.getId(armor.getItem()).toString(), true))
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

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void useMixin(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info, ItemStack itemStack, EquipmentSlot equipmentSlot,
            ItemStack itemStack2) {
        try {
            ArrayList<Object> levelList = LevelLists.customItemList;
            ArmorItem armor = (ArmorItem) (Object) this;

            if (!levelList.isEmpty() && levelList.contains(Registry.ITEM.getId(armor).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, Registry.ITEM.getId(armor).toString(), true))
                    info.setReturnValue(TypedActionResult.fail(itemStack));
            } else {
                String string = ((ArmorItem) (Object) this).getMaterial().getName().toLowerCase();
                levelList = LevelLists.armorList;
                if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, string, true)) {
                    user.sendMessage(
                            new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).formatted(Formatting.RED),
                            true);
                    info.setReturnValue(TypedActionResult.fail(itemStack));
                }
            }
        } catch (AbstractMethodError ignore) {
        }
    }
}
