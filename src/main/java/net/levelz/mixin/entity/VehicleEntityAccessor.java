package net.levelz.mixin.entity;

import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VehicleEntity.class)
public interface VehicleEntityAccessor {

    @Invoker("asItem")
    Item callAsItem();
}
