package net.levelz.editor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.levelz.data.DatapackErrors;
import net.levelz.data.RestrictionLoader;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.network.packet.EditorDataPacket;
import net.levelz.network.packet.EditorResultPacket;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class RestrictionEditor {

    private static final Logger LOGGER = LogManager.getLogger("LevelZ");

    public static boolean canEdit(ServerPlayer serverPlayerEntity) {
        return serverPlayerEntity.hasPermissions(2);
    }

    public static void sendEditorData(ServerPlayer serverPlayerEntity, boolean openScreen) {
        if (!canEdit(serverPlayerEntity)) {
            return;
        }
        List<String> skills = LevelManager.SKILLS.values().stream().map(Skill::getKey).toList();
        ServerPlayNetworking.send(serverPlayerEntity, new EditorDataPacket(List.copyOf(RestrictionLoader.ENTRIES), skills, DatapackErrors.getErrors(), openScreen));
    }

    public static void save(ServerPlayer serverPlayerEntity, String key, String json, int baseHash) {
        if (!canEdit(serverPlayerEntity)) {
            return;
        }
        if (key.isBlank()) {
            sendResult(serverPlayerEntity, key, false, "editor.levelz.invalid_key");
            return;
        }
        JsonElement element;
        try {
            element = JsonParser.parseString(json);
        } catch (Exception e) {
            sendResult(serverPlayerEntity, key, false, "editor.levelz.invalid_json");
            return;
        }
        if (!element.isJsonObject()) {
            sendResult(serverPlayerEntity, key, false, "editor.levelz.invalid_json");
            return;
        }
        JsonObject overrides = RestrictionOverrides.read(serverPlayerEntity.getServer());
        if (overrides == null) {
            sendResult(serverPlayerEntity, key, false, "editor.levelz.invalid_file");
            return;
        }
        if (RestrictionOverrides.hash(overrides.get(key)) != baseHash) {
            sendResult(serverPlayerEntity, key, false, "editor.levelz.conflict");
            return;
        }
        overrides.add(key, element);
        writeAndReload(serverPlayerEntity, key, overrides, "editor.levelz.saved");
    }

    public static void delete(ServerPlayer serverPlayerEntity, String key, int baseHash) {
        if (!canEdit(serverPlayerEntity)) {
            return;
        }
        JsonObject overrides = RestrictionOverrides.read(serverPlayerEntity.getServer());
        if (overrides == null) {
            sendResult(serverPlayerEntity, key, false, "editor.levelz.invalid_file");
            return;
        }
        if (!overrides.has(key)) {
            sendResult(serverPlayerEntity, key, false, "editor.levelz.not_found");
            return;
        }
        if (RestrictionOverrides.hash(overrides.get(key)) != baseHash) {
            sendResult(serverPlayerEntity, key, false, "editor.levelz.conflict");
            return;
        }
        overrides.remove(key);
        writeAndReload(serverPlayerEntity, key, overrides, "editor.levelz.deleted");
    }

    private static void writeAndReload(ServerPlayer serverPlayerEntity, String key, JsonObject overrides, String message) {
        MinecraftServer server = serverPlayerEntity.getServer();
        try {
            RestrictionOverrides.write(server, overrides);
        } catch (IOException e) {
            LOGGER.error("Failed to write {}", RestrictionOverrides.getPath(server), e);
            sendResult(serverPlayerEntity, key, false, "editor.levelz.write_failed");
            return;
        }
        LOGGER.info("{} changed the restriction override {}", serverPlayerEntity.getName().getString(), key);
        RestrictionLoader.reloadRestrictions(server);
        String source = " " + RestrictionLoader.getOverrideSource(key);
        List<String> problems = DatapackErrors.getErrors().stream().filter(problem -> problem.contains(source)).toList();
        ServerPlayNetworking.send(serverPlayerEntity, new EditorResultPacket(key, true, message, List.of(), problems));
        sendEditorData(serverPlayerEntity, false);
    }

    public static void export(ServerPlayer serverPlayerEntity) {
        if (!canEdit(serverPlayerEntity)) {
            return;
        }
        MinecraftServer server = serverPlayerEntity.getServer();
        JsonObject overrides = RestrictionOverrides.read(server);
        if (overrides == null) {
            sendResult(serverPlayerEntity, "", false, "editor.levelz.invalid_file");
            return;
        }
        JsonObject exported = new JsonObject();
        int skipped = 0;
        for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonObject() && value.getAsJsonObject().has("remove") && value.getAsJsonObject().get("remove").getAsBoolean()) {
                skipped++;
            } else {
                exported.add(entry.getKey(), value);
            }
        }
        Path root = server.getWorldPath(LevelResource.ROOT).resolve("levelz").resolve("export").resolve("levelz_export").normalize();
        JsonObject packInfo = new JsonObject();
        packInfo.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
        packInfo.addProperty("description", "LevelZ restrictions");
        JsonObject packMeta = new JsonObject();
        packMeta.add("pack", packInfo);
        try {
            RestrictionOverrides.writeJson(root.resolve("pack.mcmeta"), packMeta);
            RestrictionOverrides.writeJson(root.resolve("data").resolve("levelz_export").resolve("restriction").resolve("restrictions.json"), exported);
        } catch (IOException e) {
            LOGGER.error("Failed to export the restriction overrides to {}", root, e);
            sendResult(serverPlayerEntity, "", false, "editor.levelz.write_failed");
            return;
        }
        LOGGER.info("{} exported the restriction overrides to {}", serverPlayerEntity.getName().getString(), root);
        ServerPlayNetworking.send(serverPlayerEntity, new EditorResultPacket("", true, "editor.levelz.exported", List.of(root.toString(), String.valueOf(skipped)), List.of()));
    }

    private static void sendResult(ServerPlayer serverPlayerEntity, String key, boolean success, String message) {
        ServerPlayNetworking.send(serverPlayerEntity, new EditorResultPacket(key, success, message, List.of(), List.of()));
    }

}
