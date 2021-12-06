package net.levelz.mixin.block;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.levelz.access.PlayerBreakBlockAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    @Inject(method = "onBlockBreakStart", at = @At(value = "HEAD"))
    private void onBlockBreakStartMixin(World world, BlockPos pos, PlayerEntity player, CallbackInfo info) {

        // Set player inventory calculation here
        ItemStack itemStack = player.getStackInHand(player.getActiveHand());
        if (itemStack.getItem() instanceof MiningToolItem) {
            ArrayList<Object> itemList;
            if (itemStack.isIn(FabricToolTags.HOES)) {
                itemList = LevelLists.hoeList;
            } else if (itemStack.isIn(FabricToolTags.AXES)) {
                itemList = LevelLists.axeList;
            } else {
                itemList = LevelLists.toolList;
            }
            if (!PlayerStatsManager.playerLevelisHighEnough(player, itemList, ((MiningToolItem) itemStack.getItem()).getMaterial().toString().toLowerCase(), true)) {
                ((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(false);
            } else
                ((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(true);
        } else
            ((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(true);

        // Set abstract block calculation here
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        int playerMiningLevel = playerStatsManager.getLevel("mining");
        if (playerMiningLevel < ConfigInit.CONFIG.maxLevel) {
            if (playerStatsManager.lockedBlockIds.contains(Registry.BLOCK.getRawId(world.getBlockState(pos).getBlock()))) {
                ((PlayerBreakBlockAccess) player.getInventory()).setAbstractBlockBreakDelta(ConfigInit.CONFIG.miningLockedMultiplicator);
            } else if (playerMiningLevel < 5) {
                ((PlayerBreakBlockAccess) player.getInventory()).setAbstractBlockBreakDelta(1.2F - playerMiningLevel * 0.0475F);
            } else
                ((PlayerBreakBlockAccess) player.getInventory()).setAbstractBlockBreakDelta(1.0F);
        } else
            ((PlayerBreakBlockAccess) player.getInventory()).setAbstractBlockBreakDelta(1.0F);
    }

    // No player instance given to check
    // @Inject(method = "onStacksDropped", at = @At(value = "HEAD"))
    // private void onStacksDroppedMixin(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, CallbackInfo info) {
    // }

}
