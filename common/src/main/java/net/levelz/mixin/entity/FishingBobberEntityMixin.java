package net.levelz.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.levelz.access.LevelManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingHook.class)
public class FishingBobberEntityMixin {

    @WrapOperation(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 1))
    private boolean useMixin(Level instance, Entity entity, Operation<Boolean> original, @Local ItemEntity itemEntity) {
        if (ConfigInit.CONFIG.fishingXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn((ServerLevel) getPlayerOwner().level(), getPlayerOwner().position().add(0.0D, 0.5D, 0.0D),
                    (int) ((getPlayerOwner().level().getRandom().nextInt(6) + 1) * ConfigInit.CONFIG.fishingXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && getPlayerOwner() != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) getPlayerOwner()).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
        return original.call(instance, entity);
    }

    @Shadow
    @Nullable
    public Player getPlayerOwner() {
        return null;
    }
}
