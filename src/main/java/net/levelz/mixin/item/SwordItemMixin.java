package net.levelz.mixin.item;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwordItem.class)
public class SwordItemMixin {

    @Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
    private void postHitMixin(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> info) {
        if (attacker instanceof Player playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (!levelManager.hasRequiredStackLevel(stack)) {
                info.setReturnValue(false);
            }
        }

    }

    @Inject(method = "postHurtEnemy", at = @At("HEAD"), cancellable = true)
    private void postMineMixin(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo info) {
        if (attacker instanceof Player playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (!levelManager.hasRequiredStackLevel(stack)) {
                info.cancel();
            }
        }
    }

}
