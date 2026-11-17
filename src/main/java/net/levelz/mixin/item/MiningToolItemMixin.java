package net.levelz.mixin.item;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class MiningToolItemMixin {

    @Inject(method = "postHurtEnemy", at = @At("HEAD"), cancellable = true)
    private void postMineMixin(LivingEntity target, LivingEntity attacker, CallbackInfo info) {
        ItemStack stack = (ItemStack) (Object) this;
        if (attacker instanceof Player playerEntity && stack.has(DataComponents.WEAPON)) {
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
