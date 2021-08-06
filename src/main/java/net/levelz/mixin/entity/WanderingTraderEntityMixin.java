package net.levelz.mixin.entity;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(WanderingTraderEntity.class)
public class WanderingTraderEntityMixin {

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/WanderingTraderEntity;setCurrentCustomer(Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
    private void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        ArrayList<Object> levelList = LevelLists.wanderingTraderList;
        if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
            player.sendMessage(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)), true);
            info.setReturnValue(ActionResult.FAIL);
        }
    }
}
