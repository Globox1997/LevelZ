package net.levelz.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.levelz.access.LevelManagerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(BlockBehaviour.class)
public class AbstractBlockMixin {

    @WrapOperation(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDrops(Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;"))
    private List<ItemStack> onExplodedMixin(BlockState instance, LootParams.Builder builder, Operation<List<ItemStack>> original, BlockState state, Level world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.getIndirectSourceEntity() instanceof Player playerEntity && !playerEntity.isCreative() && !((LevelManagerAccess) playerEntity).getLevelManager().hasRequiredMiningLevel(state.getBlock())) {
            return List.of();
        }
        return original.call(instance, builder);
    }
}