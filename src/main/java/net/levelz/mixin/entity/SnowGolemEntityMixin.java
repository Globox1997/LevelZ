package net.levelz.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(SnowGolemEntity.class)
public class SnowGolemEntityMixin {

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SnowGolemEntity;sheared(Lnet/minecraft/sound/SoundCategory;)V"), cancellable = true)
    protected void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.snowGolemList, null, true)) {
            info.setReturnValue(ActionResult.FAIL);
        }
    }

}
