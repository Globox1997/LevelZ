package net.levelz.mixin.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.access.PlayerListAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

@SuppressWarnings("rawtypes")
@Mixin(PlayerListS2CPacket.class)
public abstract class PlayerListS2CPacketMixin implements PlayerListAccess {
    @Unique
    private Map<UUID, Integer> levelMap = new HashMap<UUID, Integer>();

    @Inject(method = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;<init>(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;[Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void playerListS2CPacketMixin(PlayerListS2CPacket.Action action, ServerPlayerEntity players[], CallbackInfo info, ServerPlayerEntity var3[], int var4, int var5,
            ServerPlayerEntity serverPlayerEntity) {
        levelMap.put(serverPlayerEntity.getUuid(), ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager().getOverallLevel());
    }

    @Inject(method = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;<init>(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void playerListS2CPacketMixin(PlayerListS2CPacket.Action action, Collection<ServerPlayerEntity> players, CallbackInfo info, Iterator var3, ServerPlayerEntity serverPlayerEntity) {
        levelMap.put(serverPlayerEntity.getUuid(), ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager().getOverallLevel());
    }

    @Inject(method = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(PacketByteBuf buf, CallbackInfo info) {
        levelMap = buf.readMap(PacketByteBuf::readUuid, PacketByteBuf::readInt);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void writeMixin(PacketByteBuf buf, CallbackInfo info) {
        buf.writeMap(levelMap, PacketByteBuf::writeUuid, PacketByteBuf::writeInt);
    }

    @Override
    public Map<UUID, Integer> getLevelMap() {
        return levelMap;
    }

}
