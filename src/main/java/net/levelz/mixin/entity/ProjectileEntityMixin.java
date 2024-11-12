package net.levelz.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    // Todo: Decide if I gonna keep something like this
//    @Shadow
//    @Nullable
//    private Entity owner;
//
//    // archer bonus
//
//    @ModifyArg(method = "Lnet/minecraft/entity/projectile/ProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;setVelocity(DDDFF)V", ordinal = 0), index = 4)
//    private float setVelocityMixin(float original) {
////        if (owner != null && owner instanceof PlayerEntity playerEntity) {
////            int archeryLevel = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager().getSkillLevel(SkillOld.ARCHERY);
////            float newAccuracy = original - 0.3f + Math.abs(archeryLevel - ConfigInit.CONFIG.maxLevel) * ConfigInit.CONFIG.archeryInaccuracyBonus;
////            if (archeryLevel < ConfigInit.CONFIG.maxLevel && newAccuracy < original && ConfigInit.CONFIG.archeryInaccuracyBonus > 0.001f) {
////                return newAccuracy;
////            }
////        }
//        return original;
//    }
}
