package net.levelz.mixin.server.world;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.network.PlayerStatsServerPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "addPlayer", at = @At(value = "TAIL"), cancellable = true)
    private void addPlayer(ServerPlayerEntity player, CallbackInfo info) {
        PlayerStatsServerPacket.writeS2CSkillPacket(((PlayerStatsManagerAccess) player).getPlayerStatsManager(), player);
    }
}
