package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerSyncAccess;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "addPlayer", at = @At(value = "TAIL"))
    private void addPlayerMixin(ServerPlayerEntity player, CallbackInfo info) {
        ((PlayerSyncAccess) player).syncStats(true);
    }
}
