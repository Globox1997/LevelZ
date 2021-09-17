package net.levelz.mixin.entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerDropAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;

@Mixin(StorageMinecartEntity.class)
public class StorageMinecartEntityMixin {

    @Inject(method = "generateLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/context/LootContext$Builder;luck(F)Lnet/minecraft/loot/context/LootContext$Builder;"))
    private void generateLootMixin(@Nullable PlayerEntity player, CallbackInfo info) {
        ((PlayerDropAccess) player).resetKilledMobStat();
    }
}
