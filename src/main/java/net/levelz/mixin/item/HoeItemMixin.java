package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

@Mixin(HoeItem.class)
public class HoeItemMixin {

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"), cancellable = true)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        ArrayList<Object> levelList = LevelLists.hoeList;
        String material = ((MiningToolItem) context.getStack().getItem()).getMaterial().toString().toLowerCase();
        if (!PlayerStatsManager.playerLevelisHighEnough(context.getPlayer(), levelList, material, true)) {
            context.getPlayer()
                    .sendMessage(Text.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                            .formatted(Formatting.RED), true);
            info.setReturnValue(ActionResult.FAIL);
        }
    }

}
