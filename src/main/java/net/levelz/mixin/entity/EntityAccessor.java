package net.levelz.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Invoker("canAddPassenger")
    boolean callCanAddPassenger(Entity passenger);

}
