package net.levelz.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.levelz.access.LevelManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
public abstract class EnderDragonEntityMixin extends Mob {

    @Unique
    @Nullable
    ServerPlayer serverPlayerEntity = null;

    public EnderDragonEntityMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V", ordinal = 0))
    protected void updatePostDeathMixin(CallbackInfo info, @Local int g) {
        if (ConfigInit.CONFIG.dragonXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn((ServerLevel) this.level(), this.position(),
                    Mth.floor((float) g * 0.08f * ConfigInit.CONFIG.dragonXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) serverPlayerEntity).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }

    @Inject(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V", ordinal = 1))
    protected void updatePostDeathXPMixin(CallbackInfo info, @Local int g) {
        if (ConfigInit.CONFIG.dragonXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn((ServerLevel) this.level(), this.position(),
                    Mth.floor((float) g * 0.2f * ConfigInit.CONFIG.dragonXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) serverPlayerEntity).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            if (source.getDirectEntity() instanceof Projectile projectileEntity) {
                if (projectileEntity.getOwner() instanceof ServerPlayer serverPlayerEntity) {
                    this.serverPlayerEntity = serverPlayerEntity;
                }
            } else if (source.getDirectEntity() instanceof ServerPlayer serverPlayerEntity) {
                this.serverPlayerEntity = serverPlayerEntity;
            }
        }
        super.die(source);
    }
}
