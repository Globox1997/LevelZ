package net.levelz.neoforge.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.levelz.LevelzMain;
import net.levelz.config.LevelzConfig;
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
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(value = LevelzMain.MOD_ID, dist = Dist.CLIENT)
public class LevelzNeoForgeClient {

    public LevelzNeoForgeClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(RegisterKeyMappingsEvent.class, event -> {
            event.registerCategory(KeyInit.CATEGORY);
            event.register(KeyInit.screenKey);
        });
        modBus.addListener(RegisterClientPayloadHandlersEvent.class, LevelzNeoForgeClient::registerPayloadHandlers);
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerEntityRenderer(EntityInit.LEVEL_EXPERIENCE_ORB, LevelExperienceOrbEntityRenderer::new));
        modBus.addListener(RegisterGuiLayersEvent.class, event -> event.registerAboveAll(RenderInit.LOCKED_BLOCK_INFO, (drawContext, deltaTracker) -> RenderInit.renderLockedBlockInfo(drawContext)));
        modBus.addListener(FMLClientSetupEvent.class, event -> event.enqueueWork(RenderInit::registerTabs));

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> KeyInit.onClientTick(Minecraft.getInstance()));
        NeoForge.EVENT_BUS.addListener(ItemTooltipEvent.class, event -> RenderInit.appendItemTooltip(event.getItemStack(), event.getToolTip()));
        NeoForge.EVENT_BUS.addListener(ExtractBlockOutlineRenderStateEvent.class, event -> {
            BlockPos pos = event.getBlockPos();
            if (RenderInit.isLockedOutline(pos)) {
                event.addCustomRenderer((outlineRenderState, submitNodeCollector, poseStack, levelRenderState) -> {
                    RenderInit.renderLockedOutline(outlineRenderState, submitNodeCollector, poseStack, levelRenderState);
                    return true;
                });
            }
        });

        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> AutoConfigClient.getConfigScreen(LevelzConfig.class, parent).get());
    }

    private static void registerPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(SkillSyncPacket.PACKET_ID, (payload, context) -> LevelClientPacket.handleSkillSync(payload));
        event.register(PlayerSkillSyncPacket.PACKET_ID, (payload, context) -> LevelClientPacket.handlePlayerSkillSync(payload));
        event.register(LevelPacket.PACKET_ID, (payload, context) -> LevelClientPacket.handleLevel(payload));
        event.register(RestrictionPacket.PACKET_ID, (payload, context) -> LevelClientPacket.handleRestriction(payload));
        event.register(StatPacket.PACKET_ID, (payload, context) -> LevelClientPacket.handleStat(payload));
        event.register(EditorDataPacket.PACKET_ID, (payload, context) -> LevelClientPacket.handleEditorData(payload));
        event.register(EditorResultPacket.PACKET_ID, (payload, context) -> LevelClientPacket.handleEditorResult(payload));
        event.register(EnchantmentZPacket.PACKET_ID, (payload, context) -> LevelClientPacket.handleEnchantmentZ(payload));
    }

}
