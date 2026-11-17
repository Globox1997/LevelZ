package net.levelz.mixin.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @WrapOperation(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;accept(Lnet/minecraft/core/Holder;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;)V"))
    private static void forEachEnchantmentMixin(EnchantmentHelper.EnchantmentInSlotVisitor instance, Holder<Enchantment> enchantmentRegistryEntry, int i, EnchantedItemInUse enchantmentEffectContext, Operation<Void> original) {
        if (ConfigInit.CONFIG.restrictEnchantedItems && enchantmentEffectContext.owner() instanceof Player playerEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (levelManager.hasRequiredEnchantmentLevel(enchantmentRegistryEntry, i)) {
                original.call(instance, enchantmentRegistryEntry, i, enchantmentEffectContext);
            }
        } else {
            original.call(instance, enchantmentRegistryEntry, i, enchantmentEffectContext);
        }
    }

    @Inject(method = "doPostAttackEffectsWithItemSource(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void onTargetDamagedMixin(ServerLevel world, Entity target, DamageSource damageSource, ItemStack weapon, CallbackInfo info) {
        if (weapon != null && damageSource.getEntity() instanceof Player playerEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (!levelManager.hasRequiredStackLevel(weapon)) {
                info.cancel();
            }
        }
    }

}
