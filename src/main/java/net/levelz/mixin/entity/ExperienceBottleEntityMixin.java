package net.levelz.mixin.entity;

import net.levelz.access.LevelManagerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

@Mixin(ThrownExperienceBottle.class)
public abstract class ExperienceBottleEntityMixin extends ThrowableItemProjectile {

    public ExperienceBottleEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownExperienceBottle;discard()V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void onCollisionMixin(HitResult hitResult, CallbackInfo info, int i) {
        if (ConfigInit.CONFIG.bottleXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn((ServerLevel) this.level(), this.position().add(0.0D, 0.5D, 0.0D),
                    (int) (i * ConfigInit.CONFIG.bottleXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && this.getOwner() != null && this.getOwner() instanceof ServerPlayer serverPlayerEntity
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) serverPlayerEntity).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }
}
