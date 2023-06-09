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
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(TrinketItem.class)
public class TrinketItemMixin {

    @Inject(method = "equipItem", at = @At("HEAD"), cancellable = true)
    private static void equipItemMixin(PlayerEntity user, ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        ArrayList<Object> levelList = LevelLists.customItemList;
        if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(stack.getItem()).toString())) {
            String string = Registries.ITEM.getId(stack.getItem()).toString();
            if (!PlayerStatsManager.playerLevelisHighEnough(user, LevelLists.customItemList, string, true)) {
                user.sendMessage(Text.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).formatted(Formatting.RED),
                        true);
                info.setReturnValue(false);
            }
        } else if (stack.getItem() instanceof ArmorItem armorItem) {
            levelList = LevelLists.armorList;
            String string = armorItem.getMaterial().getName().toLowerCase();
            if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, string, true)) {
                user.sendMessage(Text.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).formatted(Formatting.RED),
                        true);
                info.setReturnValue(false);
            }
        } else {
            levelList = LevelLists.elytraList;
            if (stack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
                user.sendMessage(Text.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.RED), true);
                info.setReturnValue(false);
            }
        }
    }
}
