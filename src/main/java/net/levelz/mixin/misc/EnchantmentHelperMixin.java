package net.levelz.mixin.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @WrapOperation(method = "forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/enchantment/EnchantmentHelper$ContextAwareConsumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper$ContextAwareConsumer;accept(Lnet/minecraft/registry/entry/RegistryEntry;ILnet/minecraft/enchantment/EnchantmentEffectContext;)V"))
    private static void forEachEnchantmentMixin(EnchantmentHelper.ContextAwareConsumer instance, RegistryEntry<Enchantment> enchantmentRegistryEntry, int i, EnchantmentEffectContext enchantmentEffectContext, Operation<Void> original) {
        if (enchantmentEffectContext.owner() != null && enchantmentEffectContext.owner() instanceof PlayerEntity playerEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (levelManager.hasRequiredEnchantmentLevel(enchantmentRegistryEntry, i)) {
                original.call(instance, enchantmentRegistryEntry, i, enchantmentEffectContext);
            }
        } else {
            original.call(instance, enchantmentRegistryEntry, i, enchantmentEffectContext);
        }
    }

    @Inject(method = "onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void onTargetDamagedMixin(ServerWorld world, Entity target, DamageSource damageSource, ItemStack weapon, CallbackInfo info) {
        if (weapon != null && damageSource.getAttacker() instanceof PlayerEntity playerEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (!levelManager.hasRequiredItemLevel(weapon.getItem())) {
                info.cancel();
            }
        }
    }

}
