package net.levelz.mixin.block;

import net.levelz.util.BonusHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BushBlock.class)
public abstract class PlantBlockMixin extends Block {

    public PlantBlockMixin(Properties settings) {
        super(settings);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide() && player != null && !player.isCreative()) {
            BonusHelper.plantDropChanceBonus(player, state, pos);
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

}
