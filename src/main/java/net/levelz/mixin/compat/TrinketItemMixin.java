package net.levelz.mixin.compat;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.trinkets.api.TrinketItem;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

@Mixin(TrinketItem.class)
public class TrinketItemMixin {

    @Inject(method = "equipItem", at = @At("HEAD"), cancellable = true)
    private static void equipItemMixin(PlayerEntity user, ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        ArrayList<Object> customList = LevelLists.customItemList;
        String string = Registry.ITEM.getId(stack.getItem()).toString();
        if (!customList.isEmpty() && !PlayerStatsManager.playerLevelisHighEnough(user, customList, string, true)) {
            user.sendMessage(Text.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2)).formatted(Formatting.RED),
                    true);
            info.setReturnValue(false);
        }
    }
}
