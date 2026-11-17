package net.levelz.neoforge;

import net.levelz.LevelzMain;
import net.levelz.access.MobEntityAccess;
import net.levelz.data.RestrictionLoader;
import net.levelz.data.SkillLoader;
import net.levelz.editor.RestrictionEditor;
import net.levelz.init.CommandInit;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod(LevelzMain.MOD_ID)
public class LevelzNeoForge {

    private static final Map<UUID, ServerPlayer> RESPAWNING_PLAYERS = new ConcurrentHashMap<>();

    public LevelzNeoForge(IEventBus modBus) {
        LevelzMain.init();

        modBus.addListener(RegisterEvent.class, LevelzNeoForge::register);
        modBus.addListener(BuildCreativeModeTabContentsEvent.class, event -> ItemInit.addToCreativeTab(event.getTabKey(), event::accept));
        modBus.addListener(RegisterPayloadHandlersEvent.class, LevelzNeoForge::registerPayloads);

        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> CommandInit.register(event.getDispatcher()));

        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                EventInit.onPlayerJoin(serverPlayer);
            }
        });
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerChangedDimensionEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                EventInit.onPlayerChangeLevel(serverPlayer);
            }
        });
        NeoForge.EVENT_BUS.addListener(PlayerEvent.Clone.class, event -> {
            if (event.getOriginal() instanceof ServerPlayer oldPlayer && event.getEntity() instanceof ServerPlayer newPlayer) {
                EventInit.onPlayerCopy(oldPlayer, newPlayer, !event.isWasDeath());
                RESPAWNING_PLAYERS.put(newPlayer.getUUID(), oldPlayer);
            }
        });
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerRespawnEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer newPlayer) {
                ServerPlayer oldPlayer = RESPAWNING_PLAYERS.remove(newPlayer.getUUID());
                if (oldPlayer != null) {
                    EventInit.onPlayerRespawn(oldPlayer, newPlayer, event.isEndConquered());
                }
            }
        });
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> RESPAWNING_PLAYERS.remove(event.getEntity().getUUID()));

        NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.RightClickItem.class, event -> {
            InteractionResult result = EventInit.onUseItem(event.getEntity(), event.getHand());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.RightClickBlock.class, event -> {
            InteractionResult result = EventInit.onUseBlock(event.getEntity(), event.getLevel(), event.getHitVec());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.EntityInteract.class, event -> {
            InteractionResult result = EventInit.onUseEntity(event.getEntity(), event.getTarget());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });

        NeoForge.EVENT_BUS.addListener(FinalizeSpawnEvent.class, event -> {
            if (event.getSpawnType() == EntitySpawnReason.SPAWNER && !event.isCanceled()) {
                ((MobEntityAccess) event.getEntity()).setSpawnerMob(true);
            }
        });

        NeoForge.EVENT_BUS.addListener(AddServerReloadListenersEvent.class, event -> {
            event.addListener(SkillLoader.ID, new SkillLoader());
            event.addListener(RestrictionLoader.ID, new RestrictionLoader(event.getServerResources().fullRegistries().lookup()));
            event.addDependency(SkillLoader.ID, RestrictionLoader.ID);
        });
        NeoForge.EVENT_BUS.addListener(TagsUpdatedEvent.class, event -> {
            if (event instanceof TagsUpdatedEvent.ServerDataLoad) {
                LoaderInit.onTagsLoaded(event.getRegistries());
            }
        });
        NeoForge.EVENT_BUS.addListener(ServerStartingEvent.class, event -> LoaderInit.onServerStarting(event.getServer()));
        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, event -> {
            if (event.getPlayer() == null) {
                LoaderInit.onDataPackReloaded(event.getPlayerList().getServer(), true);
            }
        });
    }

    private static void register(RegisterEvent event) {
        event.register(Registries.TRIGGER_TYPE, helper -> CriteriaInit.register(helper::register));
        event.register(Registries.ENTITY_TYPE, helper -> EntityInit.register(helper::register));
        event.register(Registries.ITEM, helper -> ItemInit.register(helper::register));
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SkillSyncPacket.PACKET_ID, SkillSyncPacket.PACKET_CODEC);
        registrar.playToClient(PlayerSkillSyncPacket.PACKET_ID, PlayerSkillSyncPacket.PACKET_CODEC);
        registrar.playToClient(LevelPacket.PACKET_ID, LevelPacket.PACKET_CODEC);
        registrar.playToClient(RestrictionPacket.PACKET_ID, RestrictionPacket.PACKET_CODEC);
        registrar.playToClient(EnchantmentZPacket.PACKET_ID, EnchantmentZPacket.PACKET_CODEC);

        registrar.playBidirectional(StatPacket.PACKET_ID, StatPacket.PACKET_CODEC, (payload, context) -> LevelServerPacket.handleStat((ServerPlayer) context.player(), payload));
        registrar.playToServer(AttributeSyncPacket.PACKET_ID, AttributeSyncPacket.PACKET_CODEC, (payload, context) -> LevelServerPacket.handleAttributeSync((ServerPlayer) context.player()));

        registrar.playToClient(EditorDataPacket.PACKET_ID, EditorDataPacket.PACKET_CODEC);
        registrar.playToClient(EditorResultPacket.PACKET_ID, EditorResultPacket.PACKET_CODEC);
        registrar.playToServer(EditorRequestPacket.PACKET_ID, EditorRequestPacket.PACKET_CODEC, (payload, context) -> RestrictionEditor.sendEditorData((ServerPlayer) context.player(), false));
        registrar.playToServer(EditorSavePacket.PACKET_ID, EditorSavePacket.PACKET_CODEC, (payload, context) -> RestrictionEditor.save((ServerPlayer) context.player(), payload.key(), payload.json(), payload.baseHash()));
        registrar.playToServer(EditorDeletePacket.PACKET_ID, EditorDeletePacket.PACKET_CODEC, (payload, context) -> RestrictionEditor.delete((ServerPlayer) context.player(), payload.key(), payload.baseHash()));
        registrar.playToServer(EditorExportPacket.PACKET_ID, EditorExportPacket.PACKET_CODEC, (payload, context) -> RestrictionEditor.export((ServerPlayer) context.player()));
    }

}
