package net.levelz.mixin.misc;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;

import org.spongepowered.asm.mixin.injection.At;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.world.explosion.Explosion;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow
    @Mutable
    @Final
    private Entity entity;

    public ExplosionMixin(@Nullable Entity entity) {
        this.entity = entity;
    }

    @ModifyVariable(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;toImmutable()Lnet/minecraft/util/math/BlockPos;", shift = Shift.BEFORE), ordinal = 0)
    private BlockState affectWorldMixin(BlockState original) {
        if (this.entity != null) {
            if (this.entity instanceof TntEntity && ((TntEntity) this.entity).getOwner() != null && ((TntEntity) this.entity).getOwner() instanceof PlayerEntity) {
                if (PlayerStatsManager.listContainsItemOrBlock((PlayerEntity) ((TntEntity) this.entity).getOwner(), Registries.BLOCK.getRawId(original.getBlock()), 1)) {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }
        return original;
    }
}
