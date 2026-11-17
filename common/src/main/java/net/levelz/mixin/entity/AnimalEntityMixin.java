package net.levelz.mixin.entity;

import net.levelz.access.LevelManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.util.BonusHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Animal.class)
public abstract class AnimalEntityMixin extends AgeableMob {

    public AnimalEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void breedMixin(ServerLevel world, Animal other, CallbackInfo info, AgeableMob passiveEntity) {
        if (getLoveCause() != null || other.getLoveCause() != null) {
            BonusHelper.breedTwinChanceBonus(world, getLoveCause() != null ? getLoveCause() : other.getLoveCause(), passiveEntity, other);
        }
    }

    @Inject(method = "finalizeSpawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;Lnet/minecraft/world/entity/AgeableMob;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void breedExperienceMixin(ServerLevel world, Animal other, @Nullable AgeableMob baby, CallbackInfo info) {
        if (ConfigInit.CONFIG.breedingXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn(world, this.position().add(0.0D, 0.1D, 0.0D),
                    (int) ((this.getRandom().nextInt(7) + 1) * ConfigInit.CONFIG.breedingXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && getLoveCause() != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) getLoveCause()).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }

    @Shadow
    @Nullable
    public ServerPlayer getLoveCause() {
        return null;
    }
}
