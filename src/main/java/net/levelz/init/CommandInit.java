package net.levelz.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

// word()
// literal("foo")
import java.util.Collection;
import java.util.Iterator;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
// Import everything
import static net.minecraft.server.command.CommandManager.*;

public class CommandInit {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            // dispatcher.register(CommandManager.literal("playerstats").requires((serverCommandSource) -> {
            // return serverCommandSource.hasPermissionLevel(3);
            // }).then((RequiredArgumentBuilder) CommandManager.argument("targets", EntityArgumentType.players()).then(((RequiredArgumentBuilder) CommandManager.argument("skill",
            // TextArgumentType.text())).then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
            // return execute((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "skill"),
            // IntegerArgumentType.getInteger(commandContext, "level"));
            // })))));

            // dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("time").requires((serverCommandSource) ->
            // {
            // return serverCommandSource.hasPermissionLevel(2);
            // })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("set").then(CommandManager.literal("day").executes((commandContext)
            // -> {
            // return executeSkillCommand((ServerCommandSource)commandContext.getSource(), 1000);
            // }))).then(CommandManager.literal("noon").executes((commandContext) -> {
            // return executeSkillCommand((ServerCommandSource)commandContext.getSource(), 6000);
            // }))).then(CommandManager.literal("night").executes((commandContext) -> {
            // return executeSkillCommand((ServerCommandSource)commandContext.getSource(), 13000);
            // }))).then(CommandManager.literal("midnight").executes((commandContext) -> {
            // return executeSkillCommand((ServerCommandSource)commandContext.getSource(), 18000);
            // })))))));
            // dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>) CommandManager.literal("playerstats").requires((serverCommandSource) -> {
            // return serverCommandSource.hasPermissionLevel(3);
            // }).then((RequiredArgumentBuilder) CommandManager.argument("targets", EntityArgumentType.players()).then((LiteralArgumentBuilder)
            // (CommandManager.literal("level")).then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer())).executes((commandContext) -> {
            // return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "level",
            // IntegerArgumentType.getInteger(commandContext, "level"));
            // }))).then((RequiredArgumentBuilder) CommandManager.argument("targets", EntityArgumentType.players()).then((LiteralArgumentBuilder)
            // CommandManager.literal("health").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
            // return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "health"), "health",
            // IntegerArgumentType.getInteger(commandContext, "level"));
            // })))));

            dispatcher.register((CommandManager.literal("playerstats").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(3);
            })).then(((CommandManager.argument("targets", EntityArgumentType.players())
                    .then(CommandManager.literal("level").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "level",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("points").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "points",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    })))).then(CommandManager.literal("health").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "health",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    })))).then(CommandManager.literal("strength").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "strength",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("agility").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "agility",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("defense").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "defense",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("stamina").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "stamina",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("luck").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "luck",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("archery").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "archery",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("trade").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "trade",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("smithing").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "smithing",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("mining").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "mining",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("farming").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "farming",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("building").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "building",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("progress").then((RequiredArgumentBuilder) CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand((ServerCommandSource) commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "progress",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    })))));
        });
    }

    private static int executeSkillCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String skill, int i) {
        Iterator var3 = targets.iterator();

        while (var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) var3.next();
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager(serverPlayerEntity);
            if (skill.equals("progress")) {
                playerStatsManager.levelProgress = i;
            } else {
                playerStatsManager.setLevel(skill, i);
                if (skill.equals("health")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(6D + playerStatsManager.getLevel("health"));
                    serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
                } else if (skill.equals("strength")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(1D + (double) playerStatsManager.getLevel("strength") / 5D);
                } else if (skill.equals("agility")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.09D + (double) playerStatsManager.getLevel("agility") / 1000D);
                } else if (skill.equals("defence")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue((double) playerStatsManager.getLevel("defense") / 5D);
                } else if (skill.equals("luck")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue((double) playerStatsManager.getLevel("luck") / 20D);
                }
            }
            // If mining level gets reduced, empty list has to be made
            playerStatsManager.unlockedBlocks.clear();

            PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, serverPlayerEntity);
        }
        source.sendFeedback(new TranslatableText("commands.playerstats.changed"), true);

        return targets.size();
    }

}