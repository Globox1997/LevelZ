package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        PlayerEntity playerEntity = context.getPlayer();
        ArrayList<Object> levelList;

        levelList = LevelLists.customItemList;
        if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(context.getStack().getItem()).toString())) {
            if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, Registries.ITEM.getId(context.getStack().getItem()).toString(), true))
                info.setReturnValue(ActionResult.PASS);
        } else {
            String material = ((AxeItem) context.getStack().getItem()).getMaterial().toString().toLowerCase();
            levelList = LevelLists.axeList;
            if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, material, true)) {
                playerEntity.sendMessage(
                        Text.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                                .formatted(Formatting.RED),
                        true);
                info.setReturnValue(ActionResult.PASS);
            }
        }
    }
}
