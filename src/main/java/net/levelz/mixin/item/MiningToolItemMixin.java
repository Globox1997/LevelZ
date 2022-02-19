package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(MiningToolItem.class)
public class MiningToolItemMixin {

    @Inject(method = "postHit", at = @At("HEAD"), cancellable = true)
    private void postHitMixin(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> info) {
        if (attacker instanceof PlayerEntity) {
            ArrayList<Object> levelList = null;
            if (stack.isIn(FabricToolTags.AXES))
                levelList = LevelLists.axeList;
            else if (stack.isIn(FabricToolTags.HOES))
                levelList = LevelLists.hoeList;
            else if (stack.isIn(FabricToolTags.PICKAXES) || stack.isIn(FabricToolTags.SHOVELS))
                levelList = LevelLists.toolList;
            if (levelList != null)
                if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) attacker, levelList, ((MiningToolItem) stack.getItem()).getMaterial().toString().toLowerCase(), true))
                    info.setReturnValue(false);
        }
    }

    @Inject(method = "postMine", at = @At("HEAD"), cancellable = true)
    private void postMineMixin(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> info) {
        if (miner instanceof PlayerEntity) {
            ArrayList<Object> levelList = null;
            if (stack.isIn(FabricToolTags.AXES))
                levelList = LevelLists.axeList;
            else if (stack.isIn(FabricToolTags.HOES))
                levelList = LevelLists.hoeList;
            else if (stack.isIn(FabricToolTags.PICKAXES) || stack.isIn(FabricToolTags.SHOVELS))
                levelList = LevelLists.toolList;
            if (levelList != null)
                if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) miner, levelList, ((MiningToolItem) stack.getItem()).getMaterial().toString().toLowerCase(), true))
                    info.setReturnValue(false);
        }
    }
}
