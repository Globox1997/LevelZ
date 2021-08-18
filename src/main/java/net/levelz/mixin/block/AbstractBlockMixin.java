package net.levelz.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    @ModifyVariable(method = "calcBlockBreakingDelta", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F"), ordinal = 0)
    private int calcBlockBreakingDeltaMixin(int original, BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        int playerMiningLevel = playerStatsManager.getLevel("mining");
        if (playerMiningLevel < ConfigInit.CONFIG.maxLevel) {
            if (playerStatsManager.lockedBlockIds.contains(Registry.BLOCK.getRawId(state.getBlock()))) {
                return (int) (original * ConfigInit.CONFIG.miningLockedMultiplicator);
            } else if (playerMiningLevel < 5) {
                return (int) (original * (1.2F - playerMiningLevel * 0.0475F));
            } else
                return original;
        } else
            return original;
    }
}