package net.levelz.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelItemMixin {

    @Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        if (!context.getPlayer().isCreative()) {
            int playerFarmingLevel = ((PlayerStatsManagerAccess) context.getPlayer()).getPlayerStatsManager(context.getPlayer()).getLevel("farming");
            if (playerFarmingLevel < ConfigInit.CONFIG.maxLevel) {
                if (playerFarmingLevel < 8) {
                    info.setReturnValue(ActionResult.FAIL);
                }
            }
        }
    }
}
