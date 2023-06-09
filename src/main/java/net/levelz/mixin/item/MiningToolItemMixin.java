package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(MiningToolItem.class)
public class MiningToolItemMixin {

    @Inject(method = "postHit", at = @At("HEAD"), cancellable = true)
    private void postHitMixin(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> info) {
        if (attacker instanceof PlayerEntity) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(stack.getItem()).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) attacker, levelList, Registries.ITEM.getId(stack.getItem()).toString(), true))
                    info.setReturnValue(false);
            } else {
                levelList = null;
                Item item = stack.getItem();
                if (item instanceof AxeItem)
                    levelList = LevelLists.axeList;
                else if (item instanceof HoeItem)
                    levelList = LevelLists.hoeList;
                else if (item instanceof PickaxeItem || item instanceof ShovelItem)
                    levelList = LevelLists.toolList;
                if (levelList != null)
                    if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) attacker, levelList, ((MiningToolItem) item).getMaterial().toString().toLowerCase(), true))
                        info.setReturnValue(false);
            }
        }
    }

    @Inject(method = "postMine", at = @At("HEAD"), cancellable = true)
    private void postMineMixin(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> info) {
        if (miner instanceof PlayerEntity) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(stack.getItem()).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) miner, levelList, Registries.ITEM.getId(stack.getItem()).toString(), true))
                    info.setReturnValue(false);
            } else {
                levelList = null;
                Item item = stack.getItem();
                if (item instanceof AxeItem)
                    levelList = LevelLists.axeList;
                else if (item instanceof HoeItem)
                    levelList = LevelLists.hoeList;
                else if (item instanceof PickaxeItem || item instanceof ShovelItem)
                    levelList = LevelLists.toolList;
                if (levelList != null)
                    if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) miner, levelList, ((MiningToolItem) item).getMaterial().toString().toLowerCase(), true))
                        info.setReturnValue(false);
            }
        }
    }
}
