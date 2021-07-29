package net.levelz.mixin.block;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.LevelJsonInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;"), cancellable = true)
    private static void dropStacksMixin(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo info) {
        if (entity instanceof PlayerEntity) {
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) (PlayerEntity) entity).getPlayerStatsManager((PlayerEntity) entity);
            int playerMiningLevel = playerStatsManager.getLevel("mining");
            if (playerMiningLevel < ConfigInit.CONFIG.maxLevel) {
                // List<Block> blocks = new ArrayList<Block>();
                // for (int i = 0; i < LevelJsonInit.MINING_LEVEL_LIST.size(); i++) {
                // if (LevelJsonInit.MINING_LEVEL_LIST.get(i) < playerMiningLevel) {
                // for (int u = 0; u < LevelJsonInit.MINING_BLOCK_LIST.get(i).size(); u++) {
                // blocks.add(LevelJsonInit.MINING_BLOCK_LIST.get(i).get(u));
                // }
                // }
                // TntBlock
                // }
                // if (!blocks.contains(state.getBlock())) {
                // info.cancel();
                // }
                // System.out.println(playerStatsManager.unlockedBlocks);
                System.out.println(Block.getRawIdFromState(state));
                if (!playerStatsManager.unlockedBlocks.contains(state.getBlock())) {
                    info.cancel();
                }
            }
        }
    }

}
