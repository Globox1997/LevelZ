package net.levelz.mixin.entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity {

    @Nullable
    ServerPlayerEntity serverPlayerEntity = null;

    public EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "updatePostDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void updatePostDeathMixin(CallbackInfo info, boolean f, int g) {
        if (ConfigInit.CONFIG.dragonXPMultiplier > 0.0F)
            LevelExperienceOrbEntity.spawn((ServerWorld) this.getWorld(), this.getPos(),
                    MathHelper.floor((float) g * 0.08f * ConfigInit.CONFIG.dragonXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
                                    ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager().getOverallLevel()
                                    : 1.0F)));
    }

    @Inject(method = "updatePostDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void updatePostDeathXPMixin(CallbackInfo info, boolean f, int g) {
        if (ConfigInit.CONFIG.dragonXPMultiplier > 0.0F)
            LevelExperienceOrbEntity.spawn((ServerWorld) this.getWorld(), this.getPos(),
                    MathHelper.floor((float) g * 0.2f * ConfigInit.CONFIG.dragonXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
                                    ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager().getOverallLevel()
                                    : 1.0F)));
    }

    @Override
    public void onDeath(DamageSource source) {
        if (!this.getWorld().isClient()) {
            if (source.getSource() instanceof ProjectileEntity) {
                ProjectileEntity projectileEntity = (ProjectileEntity) source.getSource();
                if (projectileEntity.getOwner() instanceof ServerPlayerEntity)
                    serverPlayerEntity = (ServerPlayerEntity) projectileEntity.getOwner();
            } else if (source.getSource() instanceof ServerPlayerEntity)
                serverPlayerEntity = (ServerPlayerEntity) source.getSource();
        }
        super.onDeath(source);
    }
}
