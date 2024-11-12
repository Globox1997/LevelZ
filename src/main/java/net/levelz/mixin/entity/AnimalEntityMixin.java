package net.levelz.mixin.entity;

import net.levelz.access.LevelManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.util.BonusHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {

    public AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "breed(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AnimalEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void breedMixin(ServerWorld world, AnimalEntity other, CallbackInfo info, PassiveEntity passiveEntity) {
        if (getLovingPlayer() != null || other.getLovingPlayer() != null) {
            BonusHelper.breedTwinChanceBonus(world, getLovingPlayer() != null ? getLovingPlayer() : other.getLovingPlayer(), passiveEntity, other);
        }
    }

    @Inject(method = "Lnet/minecraft/entity/passive/AnimalEntity;breed(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AnimalEntity;Lnet/minecraft/entity/passive/PassiveEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private void breedExperienceMixin(ServerWorld world, AnimalEntity other, @Nullable PassiveEntity baby, CallbackInfo info) {
        if (ConfigInit.CONFIG.breedingXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn(world, this.getPos().add(0.0D, 0.1D, 0.0D),
                    (int) ((this.getRandom().nextInt(7) + 1) * ConfigInit.CONFIG.breedingXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && getLovingPlayer() != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) getLovingPlayer()).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }

    @Shadow
    @Nullable
    public ServerPlayerEntity getLovingPlayer() {
        return null;
    }
}
