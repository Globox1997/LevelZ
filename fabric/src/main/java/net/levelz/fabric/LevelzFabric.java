package net.levelz.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.levelz.LevelzMain;
import net.levelz.data.RestrictionLoader;
import net.levelz.data.SkillLoader;
import net.levelz.editor.RestrictionEditor;
import net.levelz.init.CommandInit;
import net.levelz.init.CompatInit;
import net.levelz.init.CriteriaInit;
import net.levelz.init.EntityInit;
import net.levelz.init.EventInit;
import net.levelz.init.ItemInit;
import net.levelz.init.LoaderInit;
import net.levelz.network.LevelServerPacket;
import net.levelz.network.packet.AttributeSyncPacket;
import net.levelz.network.packet.EditorDataPacket;
import net.levelz.network.packet.EditorDeletePacket;
import net.levelz.network.packet.EditorExportPacket;
import net.levelz.network.packet.EditorRequestPacket;
import net.levelz.network.packet.EditorResultPacket;
import net.levelz.network.packet.EditorSavePacket;
import net.levelz.network.packet.EnchantmentZPacket;
import net.levelz.network.packet.LevelPacket;
import net.levelz.network.packet.PlayerSkillSyncPacket;
import net.levelz.network.packet.RestrictionPacket;
import net.levelz.network.packet.SkillSyncPacket;
import net.levelz.network.packet.StatPacket;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;

public class LevelzFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        LevelzMain.init();
        CompatInit.init();

        CriteriaInit.register((id, trigger) -> Registry.register(BuiltInRegistries.TRIGGER_TYPES, id, trigger));
        EntityInit.register((id, entityType) -> Registry.register(BuiltInRegistries.ENTITY_TYPE, id, entityType));
        ItemInit.register((id, item) -> Registry.register(BuiltInRegistries.ITEM, id, item));
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register((tab, output) -> BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab)
                .ifPresent(key -> ItemInit.addToCreativeTab(key, item -> output.accept(item))));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CommandInit.register(dispatcher));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> EventInit.onPlayerJoin(handler.getPlayer()));
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> EventInit.onPlayerChangeLevel(player));
        ServerPlayerEvents.COPY_FROM.register(EventInit::onPlayerCopy);
        ServerPlayerEvents.AFTER_RESPAWN.register(EventInit::onPlayerRespawn);
        UseItemCallback.EVENT.register((player, world, hand) -> EventInit.onUseItem(player, hand));
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> EventInit.onUseBlock(player, world, hitResult));
        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> EventInit.onUseEntity(player, entity));

        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(SkillLoader.ID, new SkillLoader());
        DataResourceLoader.get().registerReloadListener(RestrictionLoader.ID, RestrictionLoader::new);
        ResourceLoader.get(PackType.SERVER_DATA).addListenerOrdering(SkillLoader.ID, RestrictionLoader.ID);
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (!client) {
                LoaderInit.onTagsLoaded(registries);
            }
        });
        ServerLifecycleEvents.SERVER_STARTING.register(LoaderInit::onServerStarting);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> LoaderInit.onDataPackReloaded(server, success));

        registerPayloads();
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(SkillSyncPacket.PACKET_ID, SkillSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlayerSkillSyncPacket.PACKET_ID, PlayerSkillSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(LevelPacket.PACKET_ID, LevelPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(RestrictionPacket.PACKET_ID, RestrictionPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(EnchantmentZPacket.PACKET_ID, EnchantmentZPacket.PACKET_CODEC);

        PayloadTypeRegistry.clientboundPlay().register(StatPacket.PACKET_ID, StatPacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(StatPacket.PACKET_ID, StatPacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(AttributeSyncPacket.PACKET_ID, AttributeSyncPacket.PACKET_CODEC);

        PayloadTypeRegistry.clientboundPlay().register(EditorDataPacket.PACKET_ID, EditorDataPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(EditorResultPacket.PACKET_ID, EditorResultPacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(EditorRequestPacket.PACKET_ID, EditorRequestPacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(EditorSavePacket.PACKET_ID, EditorSavePacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(EditorDeletePacket.PACKET_ID, EditorDeletePacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(EditorExportPacket.PACKET_ID, EditorExportPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(EditorRequestPacket.PACKET_ID, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> RestrictionEditor.sendEditorData(player, false));
        });
        ServerPlayNetworking.registerGlobalReceiver(EditorSavePacket.PACKET_ID, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> RestrictionEditor.save(player, payload.key(), payload.json(), payload.baseHash()));
        });
        ServerPlayNetworking.registerGlobalReceiver(EditorDeletePacket.PACKET_ID, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> RestrictionEditor.delete(player, payload.key(), payload.baseHash()));
        });
        ServerPlayNetworking.registerGlobalReceiver(EditorExportPacket.PACKET_ID, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> RestrictionEditor.export(player));
        });
        ServerPlayNetworking.registerGlobalReceiver(StatPacket.PACKET_ID, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> LevelServerPacket.handleStat(player, payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(AttributeSyncPacket.PACKET_ID, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> LevelServerPacket.handleAttributeSync(player));
        });
    }

}
