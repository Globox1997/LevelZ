package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "use", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void useMixin(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info, ItemStack itemStack) {
        ArrayList<Object> levelList = LevelLists.customItemList;
        String string = Registry.ITEM.getId(itemStack.getItem()).toString();
        if (!levelList.isEmpty() && levelList.contains(string)) {
            if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, string, true)) {
                user.sendMessage(new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).formatted(Formatting.RED),
                        true);
                info.setReturnValue(TypedActionResult.fail(itemStack));
            }
        } else {
            levelList = LevelLists.crossbowList;
            if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
                user.sendMessage(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.RED), true);
                info.setReturnValue(TypedActionResult.fail(itemStack));
            }
        }
    }

    @Inject(method = "createArrow", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void createArrowMixin(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow, CallbackInfoReturnable<PersistentProjectileEntity> info, ArrowItem arrowItem,
            PersistentProjectileEntity persistentProjectileEntity) {
        if (entity instanceof PlayerEntity) {
            int archeryLevel = ((PlayerStatsManagerAccess) (PlayerEntity) entity).getPlayerStatsManager((PlayerEntity) entity).getLevel("archery");
            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage()
                    + (archeryLevel >= ConfigInit.CONFIG.maxLevel && ConfigInit.CONFIG.archeryDoubleDamageChance > entity.world.random.nextFloat() ? persistentProjectileEntity.getDamage() * 2D
                            : (double) archeryLevel * ConfigInit.CONFIG.archeryCrossbowExtraDamage));
        }

    }
}
