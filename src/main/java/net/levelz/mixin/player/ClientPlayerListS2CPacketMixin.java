package net.levelz.mixin.player;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.levelz.access.LevelManagerAccess;
import net.minecraft.network.RegistryByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.levelz.access.ClientPlayerListAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerListS2CPacket.class)
public abstract class ClientPlayerListS2CPacketMixin implements ClientPlayerListAccess {

    @Unique
    private Map<UUID, Integer> levelMap = new HashMap<UUID, Integer>();

    @Inject(method = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;<init>(Ljava/util/EnumSet;Ljava/util/Collection;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(EnumSet<PlayerListS2CPacket.Action> actions, Collection<ServerPlayerEntity> players, CallbackInfo info) {
        players.forEach((player) -> {
            this.levelMap.put(player.getUuid(), ((LevelManagerAccess) player).getLevelManager().getOverallLevel());
        });
    }

    @Inject(method = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;<init>(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(PlayerListS2CPacket.Action action, ServerPlayerEntity player, CallbackInfo info) {
        this.levelMap.put(player.getUuid(), ((LevelManagerAccess) player).getLevelManager().getOverallLevel());
    }

    @Inject(method = "<init>(Lnet/minecraft/network/RegistryByteBuf;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(RegistryByteBuf buf, CallbackInfo info) {
        this.levelMap = buf.readMap((bufx -> bufx.readUuid()), PacketByteBuf::readInt);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void writeMixin(RegistryByteBuf buf, CallbackInfo info) {
        buf.writeMap(this.levelMap, ((bufx, value) -> bufx.writeUuid(value)), PacketByteBuf::writeInt);
    }

    @Override
    public Map<UUID, Integer> getLevelMap() {
        return this.levelMap;
    }

}
