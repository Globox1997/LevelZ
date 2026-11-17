package net.levelz.mixin.misc;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.levelz.access.ClientPlayerListAccess;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "handlePlayerInfoUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/social/PlayerSocialManager;addPlayer(Lnet/minecraft/client/multiplayer/PlayerInfo;)V"))
    private void onPlayerListMixin(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo info, @Local PlayerInfo playerListEntry) {
        ((ClientPlayerListAccess) playerListEntry).setLevel(((ClientPlayerListAccess) packet).getLevelMap().getOrDefault(playerListEntry.getProfile().id(), 0));
    }

    @Inject(method = "handlePlayerInfoUpdate", at = @At(value = "INVOKE", target = "Ljava/util/EnumSet;iterator()Ljava/util/Iterator;"))
    private void onPlayerListGameModeMixin(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo info, @Local PlayerInfo playerListEntry) {
        if (packet.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE)) {
            ((ClientPlayerListAccess) playerListEntry).setLevel(((ClientPlayerListAccess) packet).getLevelMap().getOrDefault(playerListEntry.getProfile().id(), 0));
        }
    }

}
