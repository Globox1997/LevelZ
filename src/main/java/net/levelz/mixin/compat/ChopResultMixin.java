package net.levelz.mixin.compat;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ht.treechop.common.util.ChopResult;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ChopResult.class)
public class ChopResultMixin {

    @Shadow
    @Mutable
    @Final
    private World level;

    @Inject(method = "Lht/treechop/common/chop/ChopResult;apply(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/item/ItemStack;Z)Z", at = @At("HEAD"))
    private void applyMixin(BlockPos targetPos, ServerPlayerEntity player, ItemStack tool, boolean breakLeaves, CallbackInfoReturnable<Boolean> info) {
        if (player != null && tool.getItem() instanceof MiningToolItem) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(tool.getItem()).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, Registries.ITEM.getId(tool.getItem()).toString(), true)) {
                    player.getWorld().breakBlock(targetPos, false);
                    info.setReturnValue(false);
                }
            } else {
                if (tool.getItem() instanceof AxeItem) {
                    levelList = LevelLists.axeList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, ((AxeItem) tool.getItem()).getMaterial().toString().toLowerCase(), true)) {
                        player.getWorld().breakBlock(targetPos, false);
                        info.setReturnValue(false);
                    }
                }
            }
        }
    }
}
