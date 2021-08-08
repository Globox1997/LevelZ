package net.levelz.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.Iterator;

public class CommandInit {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register((CommandManager.literal("playerstats").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(3);
            })).then(CommandManager.argument("targets", EntityArgumentType.players())
                    .then(CommandManager.literal("level").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "level",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("points").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "points",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("health").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "health",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("strength").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "strength",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("agility").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "agility",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("defense").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "defense",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("stamina").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "stamina",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("luck").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "luck",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("archery").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "archery",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("trade").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "trade",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("smithing").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "smithing",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("mining").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "mining",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("farming").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "farming",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("alchemy").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "alchemy",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    }))).then(CommandManager.literal("progress").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "progress",
                                IntegerArgumentType.getInteger(commandContext, "level"));
                    })))));
        });
    }

    private static int executeSkillCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String skill, int i) {
        Iterator<ServerPlayerEntity> var3 = targets.iterator();

        while (var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) var3.next();
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager(serverPlayerEntity);
            if (skill.equals("progress")) {
                playerStatsManager.levelProgress = i;
            } else {
                playerStatsManager.setLevel(skill, i);
                if (skill.equals("health")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                            .setBaseValue(ConfigInit.CONFIG.healthBase + (double) playerStatsManager.getLevel("health") * ConfigInit.CONFIG.healthBonus);
                    serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
                } else if (skill.equals("strength")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                            .setBaseValue(ConfigInit.CONFIG.attackBase + (double) playerStatsManager.getLevel("strength") * ConfigInit.CONFIG.attackBonus);
                } else if (skill.equals("agility")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                            .setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerStatsManager.getLevel("agility") * ConfigInit.CONFIG.movementBonus);
                } else if (skill.equals("defense")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
                            .setBaseValue(ConfigInit.CONFIG.defenseBase + (double) playerStatsManager.getLevel("defense") * ConfigInit.CONFIG.defenseBonus);
                } else if (skill.equals("luck")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_LUCK)
                            .setBaseValue(ConfigInit.CONFIG.luckBase + (double) playerStatsManager.getLevel("luck") * ConfigInit.CONFIG.luckBonus);
                }
            }
            PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, serverPlayerEntity);
        }
        source.sendFeedback(new TranslatableText("commands.playerstats.changed"), true);

        return targets.size();
    }

}