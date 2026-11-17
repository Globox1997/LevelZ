package net.levelz.mixin.player;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.levelz.access.LevelManagerAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.levelz.access.ClientPlayerListAccess;

@Mixin(ClientboundPlayerInfoUpdatePacket.class)
public abstract class ClientPlayerListS2CPacketMixin implements ClientPlayerListAccess {

    @Unique
    private Map<UUID, Integer> levelMap = new HashMap<UUID, Integer>();

    @Inject(method = "<init>(Ljava/util/EnumSet;Ljava/util/Collection;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions, Collection<ServerPlayer> players, CallbackInfo info) {
        players.forEach((player) -> {
            this.levelMap.put(player.getUUID(), ((LevelManagerAccess) player).getLevelManager().getOverallLevel());
        });
    }

    @Inject(method = "<init>(Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket$Action;Lnet/minecraft/server/level/ServerPlayer;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(ClientboundPlayerInfoUpdatePacket.Action action, ServerPlayer player, CallbackInfo info) {
        this.levelMap.put(player.getUUID(), ((LevelManagerAccess) player).getLevelManager().getOverallLevel());
    }

    @Inject(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(RegistryFriendlyByteBuf buf, CallbackInfo info) {
        this.levelMap = buf.readMap((bufx -> bufx.readUUID()), FriendlyByteBuf::readInt);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void writeMixin(RegistryFriendlyByteBuf buf, CallbackInfo info) {
        buf.writeMap(this.levelMap, ((bufx, value) -> bufx.writeUUID(value)), FriendlyByteBuf::writeInt);
    }

    @Override
    public Map<UUID, Integer> getLevelMap() {
        return this.levelMap;
    }

}
