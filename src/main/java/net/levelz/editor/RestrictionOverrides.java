package net.levelz.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.levelz.data.DatapackErrors;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RestrictionOverrides {

    public static final String SOURCE = "world overrides";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static Path getPath(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ROOT).resolve("levelz").resolve("restrictions.json");
    }

    @Nullable
    public static JsonObject read(MinecraftServer server) {
        Path path = getPath(server);
        if (!Files.exists(path)) {
            return new JsonObject();
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }
            if (element.isJsonNull()) {
                return new JsonObject();
            }
            DatapackErrors.report("The world overrides file {} has to contain a json object.", path);
        } catch (Exception e) {
            DatapackErrors.report("The world overrides file {} is not valid json: {}", path, e.getMessage());
        }
        return null;
    }

    public static void write(MinecraftServer server, JsonObject overrides) throws IOException {
        writeJson(getPath(server), overrides);
    }

    public static void writeJson(Path path, JsonElement json) throws IOException {
        Files.createDirectories(path.getParent());
        Path temporaryPath = path.resolveSibling(path.getFileName() + ".tmp");
        Files.writeString(temporaryPath, GSON.toJson(json), StandardCharsets.UTF_8);
        Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING);
    }

    public static int hash(@Nullable JsonElement element) {
        return element == null ? 0 : element.toString().hashCode();
    }

}
