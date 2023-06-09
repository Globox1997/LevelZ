package net.levelz.mixin.compat;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dualwielding.util.DualWieldingOffhandAttack;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.Registries;

@Mixin(DualWieldingOffhandAttack.class)
public class DualWieldingOffhandAttackMixin {

    @Inject(method = "offhandAttack", at = @At("HEAD"), cancellable = true)
    private static void offhandAttackMixn(PlayerEntity playerEntity, Entity target, CallbackInfo info) {
        Item item = playerEntity.getMainHandStack().getItem();
        if (!item.equals(Items.AIR)) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(item).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, Registries.ITEM.getId(item).toString(), true)) {
                    target.damage(playerEntity.getDamageSources().playerAttack(playerEntity), 1.0F);
                    info.cancel();
                }
            } else if (item instanceof ToolItem) {
                levelList = null;
                if (item instanceof SwordItem) {
                    levelList = LevelLists.swordList;
                } else if (item instanceof AxeItem)
                    levelList = LevelLists.axeList;
                else if (item instanceof HoeItem)
                    levelList = LevelLists.hoeList;
                else if (item instanceof PickaxeItem || item instanceof ShovelItem)
                    levelList = LevelLists.toolList;
                if (levelList != null)
                    if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((ToolItem) item).getMaterial().toString().toLowerCase(), true)) {
                        target.damage(playerEntity.getDamageSources().playerAttack(playerEntity), 1.0F);
                        info.cancel();
                    }
            }
        }

    }
}
