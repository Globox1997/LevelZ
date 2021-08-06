package net.levelz.mixin.entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {

    public AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "breed", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void breedMixin(ServerWorld world, AnimalEntity other, CallbackInfo info, PassiveEntity passiveEntity) {
        System.out.println(getLovingPlayer() + "::" + other.getLovingPlayer());
        if (getLovingPlayer() != null || other.getLovingPlayer() != null) {
            PlayerEntity playerEntity = getLovingPlayer() != null ? getLovingPlayer() : other.getLovingPlayer();
            if (((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager(playerEntity).getLevel("farming") == ConfigInit.CONFIG.maxLevel
                    && world.random.nextFloat() < ConfigInit.CONFIG.farmingTwinChance) {
                PassiveEntity extraPassiveEntity = this.createChild(world, other);
                extraPassiveEntity.setBaby(true);
                extraPassiveEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
                world.spawnEntityAndPassengers(extraPassiveEntity);
            }
        }

    }

    @Shadow
    @Nullable
    public ServerPlayerEntity getLovingPlayer() {
        return null;
    }
}
