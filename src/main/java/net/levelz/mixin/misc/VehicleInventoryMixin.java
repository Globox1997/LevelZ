package net.levelz.mixin.misc;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.levelz.access.PlayerDropAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;

@Mixin(ContainerEntity.class)
public interface VehicleInventoryMixin {

    @Inject(method = "unpackChestVehicleLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/ContainerEntity;setLootTable(Lnet/minecraft/resources/ResourceKey;)V"))
    default void generateInventoryLootMixin(@Nullable Player player, CallbackInfo info) {
        if (player != null) {
            ((PlayerDropAccess) player).resetKilledMobStat();
        }
    }
}
