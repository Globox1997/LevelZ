package net.levelz.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.levelz.access.LevelManagerAccess;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    @WrapOperation(method = "onExploded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getDroppedStacks(Lnet/minecraft/loot/context/LootContextParameterSet$Builder;)Ljava/util/List;"))
    private List<ItemStack> onExplodedMixin(BlockState instance, LootContextParameterSet.Builder builder, Operation<List<ItemStack>> original, BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.getCausingEntity() instanceof PlayerEntity playerEntity && !playerEntity.isCreative() && !((LevelManagerAccess) playerEntity).getLevelManager().hasRequiredMiningLevel(state.getBlock())) {
            return List.of();
        }
        return original.call(instance, builder);
    }
}