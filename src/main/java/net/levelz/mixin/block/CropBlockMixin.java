package net.levelz.mixin.block;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.TagInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock {

    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!world.isClient && player != null && !player.isCreative()) {
            int farmingLevel = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player).getLevel("farming");
            if (farmingLevel >= ConfigInit.CONFIG.farmingBase && (float) farmingLevel * ConfigInit.CONFIG.farmingChanceBonus > world.random.nextFloat()) {
                List<ItemStack> list = Block.getDroppedStacks(state, (ServerWorld) world, pos, null);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isIn(TagInit.FARM_ITEMS)) {
                        Block.dropStack(world, pos, list.get(i));
                        break;
                    }
                }
            }
        }

    }
}
