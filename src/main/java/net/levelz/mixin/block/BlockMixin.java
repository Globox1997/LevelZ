package net.levelz.mixin.block;

import net.levelz.access.LevelManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.EntityInit;
import net.levelz.init.TagInit;
import net.levelz.level.LevelManager;
import net.levelz.util.BonusHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow
    private BlockState defaultBlockState;

    @Unique
    @Nullable
    private static ServerPlayer breakingPlayer = null;

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;"), cancellable = true)
    private static void dropStacksMixin(BlockState state, Level world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo info) {
        if (entity instanceof Player playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            if (EntityInit.isRedstoneBitsLoaded && entity.getClass().getName().contains("RedstoneBitsFakePlayer")) {
            } else {
                LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
                if (!levelManager.hasRequiredMiningLevel(state.getBlock())) {
                    info.cancel();
                } else if (!levelManager.hasRequiredStackLevel(stack)) {
                    info.cancel();
                }
            }
        }
    }

    @Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDrops(Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getDroppedStacksMixin(BlockState state, ServerLevel world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemInstance stack, CallbackInfoReturnable<List<ItemStack>> info, LootParams.Builder builder) {
        if (entity instanceof Player playerEntity) {
            BonusHelper.miningDropChanceBonus(playerEntity, state, pos, builder);
        }
    }

    @Inject(method = "popExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    protected void dropExperienceMixin(ServerLevel world, BlockPos pos, int size, CallbackInfo info) {
        if (ConfigInit.CONFIG.oreXPMultiplier > 0.0F && !this.defaultBlockState.is(TagInit.RESTRICTED_ORE_EXPERIENCE_BLOCKS)) {
            LevelExperienceOrbEntity.spawn(world, Vec3.atCenterOf(pos),
                    (int) (size * ConfigInit.CONFIG.oreXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && breakingPlayer != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) breakingPlayer).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }

    @Inject(method = "playerDestroy", at = @At(value = "HEAD"))
    private void afterBreakMixin(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool, CallbackInfo info) {
        breakingPlayer = player;
    }

    @Inject(method = "playerDestroy", at = @At(value = "RETURN"))
    private void afterBreakReturnMixin(ServerLevel world, ServerPlayer player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool, CallbackInfo info) {
        breakingPlayer = null;
    }

}
