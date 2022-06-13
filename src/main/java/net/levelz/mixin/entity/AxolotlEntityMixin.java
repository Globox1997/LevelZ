package net.levelz.mixin.entity;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

@Mixin(AxolotlEntity.class)
public class AxolotlEntityMixin {

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void interactMobMixin(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (player.getStackInHand(hand).getItem() == Items.WATER_BUCKET) {
            ArrayList<Object> levelList = LevelLists.axolotlList;
            if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
                player.sendMessage(Text.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.RED), true);
                info.setReturnValue(ActionResult.FAIL);
            }
        }
    }
}
