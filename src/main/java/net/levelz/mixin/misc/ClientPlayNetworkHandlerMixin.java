package net.levelz.mixin.misc;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerListAccess;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

@SuppressWarnings("rawtypes")
@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/SocialInteractionsManager;setPlayerOnline(Lnet/minecraft/client/network/PlayerListEntry;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onPlayerListMixin(PlayerListS2CPacket packet, CallbackInfo info, Iterator var2, PlayerListS2CPacket.Entry entry, PlayerListEntry playerListEntry) {
        ((PlayerListAccess) playerListEntry).setLevel(((PlayerListAccess) packet).getLevelMap().get(playerListEntry.getProfile().getId()));
    }

    @Inject(method = "onPlayerList", at = @At(value = "INVOKE", target = "Ljava/util/EnumSet;iterator()Ljava/util/Iterator;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onPlayerListGameModeMixin(PlayerListS2CPacket packet, CallbackInfo info, Iterator var2, PlayerListS2CPacket.Entry entry, PlayerListEntry playerListEntry) {
        if (packet.getActions().contains(PlayerListS2CPacket.Action.UPDATE_GAME_MODE)) {
            ((PlayerListAccess) playerListEntry).setLevel(((PlayerListAccess) packet).getLevelMap().get(playerListEntry.getProfile().getId()));
        }
    }

}
