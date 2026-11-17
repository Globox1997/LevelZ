package net.levelz.mixin.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.ClientPlayerAccess;
import net.levelz.access.ClientPlayerListAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public class PlayerEntityRendererMixin {

    @WrapOperation(method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"))
    private void renderLabelIfPresentMixin(PlayerRenderer instance, AbstractClientPlayer abstractClientPlayerEntity, Component text, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, float f, Operation<Void> original) {
        if (!((ClientPlayerAccess) abstractClientPlayerEntity).shouldRenderClientName()) {
            return;
        }
        if (ConfigInit.CONFIG.showLevel) {
            original.call(instance, abstractClientPlayerEntity, PlayerTeam.formatNameForTeam(abstractClientPlayerEntity.getTeam(),
                    Component.translatable("text.levelz.scoreboard", ((ClientPlayerListAccess) abstractClientPlayerEntity).getLevel(), abstractClientPlayerEntity.getName())), matrixStack, vertexConsumerProvider, i, f);
        } else {
            original.call(instance, abstractClientPlayerEntity, text, matrixStack, vertexConsumerProvider, i, f);
        }
    }

}
