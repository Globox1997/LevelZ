package net.levelz.mixin.entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.Skill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

@Mixin(TntEntity.class)
public abstract class TntEntityMixin extends Entity {

    @Shadow
    @Nullable
    private LivingEntity causingEntity;

    public TntEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "explode", at = @At(value = "HEAD"), cancellable = true)
    private void explodeMixin(CallbackInfo info) {
        if (causingEntity != null && causingEntity instanceof PlayerEntity player) {
            if (((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.MINING) >= ConfigInit.CONFIG.maxLevel) {
                this.getWorld().createExplosion(this, this.getX(), this.getBodyY(0.0625D), this.getZ(), 4.0F * (1F + ConfigInit.CONFIG.miningTntBonus), ExplosionSourceType.TNT);
                info.cancel();
            }
        }
    }
}
