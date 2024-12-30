package net.levelz.mixin.compat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class TieredCompatMixin extends Entity {

    @Mutable
    @Shadow
    @Final
    private static TrackedData<Float> HEALTH;

    public TieredCompatMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "readCustomDataFromNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    private void readCustomDataFromNbtMixin(LivingEntity livingEntity, float health) {
        this.dataTracker.set(HEALTH, health);
    }

}
