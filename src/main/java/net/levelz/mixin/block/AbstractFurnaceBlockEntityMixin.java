package net.levelz.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.init.ConfigInit;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Inject(method = "dropExperience", at = @At(value = "HEAD"), cancellable = true)
    private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience, CallbackInfo info) {
        if (ConfigInit.CONFIG.disableFurnaceXP)
            info.cancel();
    }

}
