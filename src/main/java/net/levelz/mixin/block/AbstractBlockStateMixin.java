package net.levelz.mixin.block;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerBreakBlockAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    // Set player inventory calculation here
    @Inject(method = "onBlockBreakStart", at = @At(value = "HEAD"))
    private void onBlockBreakStartMixin(World world, BlockPos pos, PlayerEntity player, CallbackInfo info) {

        Item item = player.getStackInHand(player.getActiveHand()).getItem();
        ArrayList<Object> levelList = LevelLists.customItemList;

        if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(item).toString())) {
            if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, Registries.ITEM.getId(item).toString(), true))
                ((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(false);
        } else if (item instanceof MiningToolItem) {
            if (item instanceof HoeItem) {
                levelList = LevelLists.hoeList;
            } else if (item instanceof AxeItem) {
                levelList = LevelLists.axeList;
            } else {
                levelList = LevelLists.toolList;
            }
            if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, ((MiningToolItem) item).getMaterial().toString().toLowerCase(), true)) {
                ((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(false);
            } else
                ((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(true);
        } else
            ((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(true);

        // Set abstract block calculation here
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
        int playerMiningLevel = playerStatsManager.getSkillLevel(Skill.MINING);
        if (playerMiningLevel < ConfigInit.CONFIG.maxLevel) {
            if (PlayerStatsManager.listContainsItemOrBlock(player, 1, Registries.BLOCK.getRawId(world.getBlockState(pos).getBlock()))) {
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
