package net.levelz.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class DatapackErrors {

    private static final Logger LOGGER = LogManager.getLogger("LevelZ");
    private static final List<String> ERRORS = new ArrayList<>();
    private static int skillErrorCount = 0;

    public static void clear() {
        ERRORS.clear();
        skillErrorCount = 0;
    }

    public static void markSkillErrors() {
        skillErrorCount = ERRORS.size();
    }

    public static void clearRestrictionErrors() {
        while (ERRORS.size() > skillErrorCount) {
            ERRORS.removeLast();
        }
    }

    public static List<String> getErrors() {
        return List.copyOf(ERRORS);
    }

    public static void report(String format, Object... args) {
        String message = new ParameterizedMessage(format, args).getFormattedMessage();
        LOGGER.warn("[Datapack] {}", message);
        ERRORS.add(message);
    }

    public static void notify(ServerPlayer serverPlayerEntity) {
        if (!ERRORS.isEmpty() && serverPlayerEntity.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            serverPlayerEntity.sendSystemMessage(Component.translatable("text.levelz.datapack_errors", ERRORS.size()).withStyle(ChatFormatting.RED));
        }
    }

}
