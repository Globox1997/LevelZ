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
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        PlayerEntity playerEntity = context.getPlayer();
        ArrayList<Object> levelList;
        String material = ((AxeItem) context.getStack().getItem()).getMaterial().toString().toLowerCase();

        levelList = LevelLists.customItemList;
        if (!levelList.isEmpty() && !PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, Registry.ITEM.getId(context.getStack().getItem()).toString(), true))
            info.setReturnValue(ActionResult.PASS);
        else {
            levelList = LevelLists.axeList;
            if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, material, true)) {
                context.getPlayer().sendMessage(
                        Text.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                                .formatted(Formatting.RED),
                        true);
                info.setReturnValue(ActionResult.PASS);
            }
        }
    }
}
