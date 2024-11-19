package net.levelz.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.levelz.access.LevelManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.EntityInit;
import net.levelz.level.LevelManager;
import net.levelz.util.BonusHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {

    @Unique
    @Nullable
    private ServerPlayerEntity serverPlayerEntity = null;

    @Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;"), cancellable = true)
    private static void dropStacksMixin(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo info) {
        if (entity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            if (EntityInit.isRedstoneBitsLoaded && entity.getClass().getName().contains("RedstoneBitsFakePlayer")) {
            } else {
                LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
                if (!levelManager.hasRequiredMiningLevel(state.getBlock())) {
                    info.cancel();
                } else if (!levelManager.hasRequiredItemLevel(stack.getItem())) {
                    info.cancel();
                }
            }
        }
    }

    @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getDroppedStacks(Lnet/minecraft/loot/context/LootContextParameterSet$Builder;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getDroppedStacksMixin(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> info, LootContextParameterSet.Builder builder) {
        if (entity instanceof PlayerEntity playerEntity) {
            BonusHelper.miningDropChanceBonus(playerEntity, state, pos, builder);
        }
    }

    @Inject(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"))
    protected void dropExperienceMixin(ServerWorld world, BlockPos pos, int size, CallbackInfo info) {
        if (ConfigInit.CONFIG.oreXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos),
                    (int) (size * ConfigInit.CONFIG.oreXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && this.serverPlayerEntity != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) this.serverPlayerEntity).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }

    @Inject(method = "onBreak", at = @At(value = "HEAD"))
    private void onBreakMixin(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> info) {
        if (!world.isClient()) {
            this.serverPlayerEntity = (ServerPlayerEntity) player;
        }
    }

}
