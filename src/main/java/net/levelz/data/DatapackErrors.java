package net.levelz.data;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.ArrayList;
import java.util.List;

public class DatapackErrors {

    private static final Logger LOGGER = LogManager.getLogger("LevelZ");
    private static final List<String> ERRORS = new ArrayList<>();

    public static void clear() {
        ERRORS.clear();
    }

    public static void report(String format, Object... args) {
        String message = new ParameterizedMessage(format, args).getFormattedMessage();
        LOGGER.warn("[Datapack] {}", message);
        ERRORS.add(message);
    }

    public static void notify(ServerPlayerEntity serverPlayerEntity) {
        if (!ERRORS.isEmpty() && serverPlayerEntity.hasPermissionLevel(2)) {
            serverPlayerEntity.sendMessage(Text.translatable("text.levelz.datapack_errors", ERRORS.size()).formatted(Formatting.RED), false);
        }
    }

}
