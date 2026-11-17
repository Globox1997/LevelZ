package net.levelz.mixin.player;

import net.levelz.access.ClientPlayerAccess;
import net.levelz.access.ClientPlayerListAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void extractRenderStateMixin(Avatar avatar, AvatarRenderState state, float partialTicks, CallbackInfo info) {
        if (!(avatar instanceof AbstractClientPlayer abstractClientPlayerEntity)) {
            return;
        }
        if (!((ClientPlayerAccess) abstractClientPlayerEntity).shouldRenderClientName()) {
            state.nameTag = null;
            state.scoreText = null;
            return;
        }
        if (ConfigInit.CONFIG.showLevel && state.nameTag != null) {
            state.nameTag = PlayerTeam.formatNameForTeam(abstractClientPlayerEntity.getTeam(),
                    Component.translatable("text.levelz.scoreboard", ((ClientPlayerListAccess) abstractClientPlayerEntity).getLevel(), abstractClientPlayerEntity.getName()));
        }
    }

}
