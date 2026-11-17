package net.levelz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.levelz.entity.render.LevelExperienceOrbEntityRenderer;
import net.levelz.init.EntityInit;
import net.levelz.init.KeyInit;
import net.levelz.init.RenderInit;
import net.levelz.network.LevelClientPacket;
import net.levelz.network.packet.EditorDataPacket;
import net.levelz.network.packet.EditorResultPacket;
import net.levelz.network.packet.EnchantmentZPacket;
import net.levelz.network.packet.LevelPacket;
import net.levelz.network.packet.PlayerSkillSyncPacket;
import net.levelz.network.packet.RestrictionPacket;
import net.levelz.network.packet.SkillSyncPacket;
import net.levelz.network.packet.StatPacket;
import net.minecraft.client.KeyMapping;

public class LevelzClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyMapping.Category.register(KeyInit.CATEGORY.id());
        KeyMappingHelper.registerKeyMapping(KeyInit.screenKey);
        ClientTickEvents.END_CLIENT_TICK.register(KeyInit::onClientTick);

        ClientPlayNetworking.registerGlobalReceiver(SkillSyncPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> LevelClientPacket.handleSkillSync(payload)));
        ClientPlayNetworking.registerGlobalReceiver(PlayerSkillSyncPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> LevelClientPacket.handlePlayerSkillSync(payload)));
        ClientPlayNetworking.registerGlobalReceiver(LevelPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> LevelClientPacket.handleLevel(payload)));
        ClientPlayNetworking.registerGlobalReceiver(RestrictionPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> LevelClientPacket.handleRestriction(payload)));
        ClientPlayNetworking.registerGlobalReceiver(StatPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> LevelClientPacket.handleStat(payload)));
        ClientPlayNetworking.registerGlobalReceiver(EditorDataPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> LevelClientPacket.handleEditorData(payload)));
        ClientPlayNetworking.registerGlobalReceiver(EditorResultPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> LevelClientPacket.handleEditorResult(payload)));
        ClientPlayNetworking.registerGlobalReceiver(EnchantmentZPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> LevelClientPacket.handleEnchantmentZ(payload)));

        EntityRendererRegistry.register(EntityInit.LEVEL_EXPERIENCE_ORB, LevelExperienceOrbEntityRenderer::new);
        RenderInit.registerTabs();
        HudElementRegistry.addLast(RenderInit.LOCKED_BLOCK_INFO, (drawContext, tickDelta) -> RenderInit.renderLockedBlockInfo(drawContext));
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> RenderInit.appendItemTooltip(stack, lines));
        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outlineRenderState) -> {
            if (!RenderInit.isLockedOutline(outlineRenderState.pos())) {
                return true;
            }
            RenderInit.renderLockedOutline(outlineRenderState, context.submitNodeCollector(), context.poseStack(), context.levelState());
            return false;
        });
    }
}
