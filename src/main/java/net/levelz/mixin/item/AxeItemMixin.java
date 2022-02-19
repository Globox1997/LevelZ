package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack stack = context.getStack();
        ArrayList<Object> levelList = LevelLists.axeList;
        String material = ((AxeItem) stack.getItem()).getMaterial().toString().toLowerCase();
        if (stack.isIn(FabricToolTags.AXES) && !PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, material, true)) {
            context.getPlayer().sendMessage(
                    new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                            .formatted(Formatting.RED),
                    true);
            info.setReturnValue(ActionResult.PASS);
        }
    }
}
