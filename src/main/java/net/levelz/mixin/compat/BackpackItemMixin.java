package net.levelz.mixin.compat;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import draylar.inmis.item.BackpackItem;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

@Mixin(BackpackItem.class)
public class BackpackItemMixin {

    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
    private static void openScreenMixin(PlayerEntity player, ItemStack backpackItemStack, CallbackInfo info) {
        if (player.getWorld() != null && !player.getWorld().isClient()) {
            ArrayList<Object> customList = LevelLists.customItemList;
            if (!customList.isEmpty() && customList.contains(Registries.ITEM.getId(backpackItemStack.getItem()).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough(player, customList, Registries.ITEM.getId(backpackItemStack.getItem()).toString(), true)) {
                    info.cancel();
                }
            }
        }
    }
}
