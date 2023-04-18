package net.levelz.mixin.entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.Skill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Shadow
    @Nullable
    private Entity owner;

    @ModifyArg(method = "Lnet/minecraft/entity/projectile/ProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;setVelocity(DDDFF)V", ordinal = 0), index = 4)
    private float setVelocityMixin(float original) {
        if (owner != null && owner instanceof PlayerEntity playerEntity) {
            int archeryLevel = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager().getSkillLevel(Skill.ARCHERY);
            float newAccuracy = original - 0.3f + Math.abs(archeryLevel - ConfigInit.CONFIG.maxLevel) * ConfigInit.CONFIG.archeryInaccuracyBonus;
            if (archeryLevel < ConfigInit.CONFIG.maxLevel && newAccuracy < original && ConfigInit.CONFIG.archeryInaccuracyBonus > 0.001f) {
                return newAccuracy;
            }
        }
        return original;
    }
}
