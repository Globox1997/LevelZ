package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.ClientPlayerListAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;

@Environment(EnvType.CLIENT)
@Mixin(PlayerTabOverlay.class)
public class PlayerListHudMixin {

    @Inject(method = "getNameForDisplay", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void getPlayerNameMixin(PlayerInfo entry, CallbackInfoReturnable<Component> info) {
        if (ConfigInit.CONFIG.showLevelList)
            info.setReturnValue(this.decorateName(entry,
                    PlayerTeam.formatNameForTeam(entry.getTeam(), Component.translatable("text.levelz.scoreboard", ((ClientPlayerListAccess) entry).getLevel(), entry.getProfile().getName()))));
    }

    @Shadow
    private Component decorateName(PlayerInfo entry, MutableComponent name) {
        return null;
    }

}
