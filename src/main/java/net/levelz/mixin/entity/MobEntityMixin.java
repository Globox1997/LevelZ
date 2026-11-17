package net.levelz.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.levelz.access.MobEntityAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobEntityMixin implements MobEntityAccess {

    @Unique
    private boolean spawnerMob = false;

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(CompoundTag nbt, CallbackInfo info) {
        this.spawnerMob = nbt.getBoolean("SpawnerMob");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(CompoundTag nbt, CallbackInfo info) {
        nbt.putBoolean("SpawnerMob", this.spawnerMob);
    }

    @Override
    public void setSpawnerMob(boolean spawnerMob) {
        this.spawnerMob = spawnerMob;
    }

    @Override
    public boolean isSpawnerMob() {
        return this.spawnerMob;
    }

}
