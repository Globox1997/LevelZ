package net.levelz.config;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.levelz.init.ConfigInit;
import net.levelz.init.LevelZComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class LevelRule implements Component, AutoSyncedComponent {
    private static final String CONFIGURED = "configured";
    private static LevelRule instance;
    private static Scoreboard scoreboard;
    //private Integer maxLevel;
    private Boolean useVanillaExp;

    public static void registry(ServerScoreboard provider) {
        if (LevelRule.scoreboard == null)
            LevelRule.scoreboard = provider;
        instance = LevelZComponent.getLevelRule(scoreboard);
        if (instance.useVanillaExp() == null)
            instance.useVanillaExp = ConfigInit.CONFIG.useVanillaExp;
    }

    public static LevelRule getInstance() {
        return instance;
    }

    public Boolean useVanillaExp() {
        return useVanillaExp;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        String configured = tag.getString(CONFIGURED);
        for (Field field : LevelRule.class.getDeclaredFields()) {
            String fieldName = field.getName();
            if (!configured.contains(fieldName)) continue;
            try {
                this.setValue(fieldName, switch (field.getType().getSimpleName()) {
                    case "Integer", "int" -> tag.getInt(fieldName);
                    case "Long", "long" -> tag.getLong(fieldName);
                    case "Float", "float" -> tag.getFloat(fieldName);
                    case "Double", "double" -> tag.getDouble(fieldName);
                    case "Boolean", "boolean" -> tag.getBoolean(fieldName);
                    case "String" -> tag.getString(fieldName);
                    default -> throw new IllegalArgumentException();
                });
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        Set<String> configured = new HashSet<>();
        for (Field field : LevelRule.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            try {
                String fieldName = field.getName();
                Object value = field.get(this);
                if (value == null) continue;
                switch (field.getType().getSimpleName()) {
                    case "Integer", "int" -> tag.putInt(fieldName, (Integer) value);
                    case "Long", "long" -> tag.putLong(fieldName, (Long) value);
                    case "Float", "float" -> tag.putFloat(fieldName, (Float) value);
                    case "Double", "double" -> tag.putDouble(fieldName, (Double) value);
                    case "Boolean", "boolean" -> tag.putBoolean(fieldName, (Boolean) value);
                    case "String" -> tag.putString(fieldName, (String) value);
                }
                configured.add(fieldName);
            } catch (IllegalAccessException ignored) {
            }
        }
        tag.putString(CONFIGURED, String.join(",", configured));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> commandBuilder(String literal, int permissionLevel) {
        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(literal).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(permissionLevel));
        for (Field field : LevelRule.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            builder.then(CommandManager.literal(field.getName()).then(
                    switch (field.getType().getSimpleName()) {
                        case "Integer", "int" -> CommandManager.argument(field.getName(), IntegerArgumentType.integer(0))
                                .executes(context -> executeRuleCommand(context.getSource(), field.getName(), IntegerArgumentType.getInteger(context, field.getName())));
                        case "Long", "long" -> CommandManager.argument(field.getName(), LongArgumentType.longArg(0))
                                .executes(context -> executeRuleCommand(context.getSource(), field.getName(), IntegerArgumentType.getInteger(context, field.getName())));
                        case "Float", "float" -> CommandManager.argument(field.getName(), FloatArgumentType.floatArg(0))
                                .executes(context -> executeRuleCommand(context.getSource(), field.getName(), FloatArgumentType.getFloat(context, field.getName())));
                        case "Double", "double" -> CommandManager.argument(field.getName(), DoubleArgumentType.doubleArg(0))
                                .executes(context -> executeRuleCommand(context.getSource(), field.getName(), DoubleArgumentType.getDouble(context, field.getName())));
                        case "Boolean", "boolean" -> CommandManager.argument(field.getName(), BoolArgumentType.bool())
                                .executes(context -> executeRuleCommand(context.getSource(), field.getName(), BoolArgumentType.getBool(context, field.getName())));
                        default -> CommandManager.argument(field.getName(), StringArgumentType.string())
                                .executes(context -> executeRuleCommand(context.getSource(), field.getName(), StringArgumentType.getString(context, field.getName())));
                    })
            );
        }
        return builder;
    }

    private static int executeRuleCommand(ServerCommandSource source, String rule, Object value) {
        Text feedback = null;
        try {
            instance.setValue(rule, value);
            feedback = new TranslatableText("commands.levelz.rule.changed");
        } catch (IllegalAccessException e) {
            feedback = new TranslatableText("commands.levelz.rule.error");
        } catch (NoSuchFieldException e) {
            feedback = new TranslatableText("commands.levelz.rule.notfound");
        } finally {
            source.sendFeedback(feedback, true);
        }
        return 0;
    }

    public void setValue(String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = LevelRule.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(this, value);
    }
}
