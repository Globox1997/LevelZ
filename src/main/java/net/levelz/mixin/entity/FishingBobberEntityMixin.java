package net.levelz.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.levelz.access.LevelManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 1))
    private boolean useMixin(World instance, Entity entity, Operation<Boolean> original, @Local ItemEntity itemEntity) {
        if (ConfigInit.CONFIG.fishingXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn((ServerWorld) getPlayerOwner().getWorld(), getPlayerOwner().getPos().add(0.0D, 0.5D, 0.0D),
                    (int) ((getPlayerOwner().getWorld().getRandom().nextInt(6) + 1) * ConfigInit.CONFIG.fishingXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && getPlayerOwner() != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) getPlayerOwner()).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
        return original.call(instance, entity);
    }

    @Shadow
    @Nullable
    public PlayerEntity getPlayerOwner() {
        return null;
    }
}
