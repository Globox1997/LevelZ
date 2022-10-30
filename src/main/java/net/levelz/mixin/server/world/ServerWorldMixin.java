package net.levelz.mixin.server.world;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.network.PlayerStatsServerPacket;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    private final Map<Entity, Integer> TO_BE_SYNCED = new HashMap<>();

    @Inject(method = "addPlayer", at = @At(value = "TAIL"), cancellable = true)
    private void addPlayer(ServerPlayerEntity player, CallbackInfo info) {
        TO_BE_SYNCED.put(player, 0);
    }

    @Inject(method = "tickEntity", at = @At(value = "TAIL"), cancellable = true)
    public void tickEntity(Entity entity, CallbackInfo info) {
        if (!TO_BE_SYNCED.containsKey(entity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        Integer playerTick = TO_BE_SYNCED.get(player);
        if (playerTick != null && playerTick < 20) {
            TO_BE_SYNCED.put(player, ++playerTick);
            return;
        }
        PlayerStatsServerPacket.writeS2CSkillPacket(((PlayerStatsManagerAccess) player).getPlayerStatsManager(), player);
        TO_BE_SYNCED.remove(player);
    }
}
