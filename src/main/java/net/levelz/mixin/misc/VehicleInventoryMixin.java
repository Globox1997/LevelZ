package net.levelz.mixin.misc;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.levelz.access.PlayerDropAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleInventory;

@Mixin(VehicleInventory.class)
public interface VehicleInventoryMixin {

    @Inject(method = "generateInventoryLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/VehicleInventory;setLootTableId(Lnet/minecraft/util/Identifier;)V"))
    default void generateInventoryLootMixin(@Nullable PlayerEntity player, CallbackInfo info) {
        if (player != null)
            ((PlayerDropAccess) player).resetKilledMobStat();
    }
}
