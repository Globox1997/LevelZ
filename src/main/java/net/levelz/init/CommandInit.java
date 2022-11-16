package net.levelz.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.commons.lang3.StringUtils;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CommandInit {

    public static void init() {

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register((CommandManager.literal("info").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(3);
            })).then(CommandManager.literal("material").executes((commandContext) -> {
                return executeInfoMaterial(commandContext.getSource());
            })));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register((CommandManager.literal("playerstats").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(3);
            })).then(CommandManager.argument("targets", EntityArgumentType.players())
                    // Add values
                    .then(CommandManager.literal("add").then(CommandManager.literal("level").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "level",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("points").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "points",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("health").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "health",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("strength").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "strength",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("agility").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "agility",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("defense").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "defense",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("stamina").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "stamina",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("luck").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "luck",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("archery").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "archery",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("trade").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "trade",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("smithing").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "smithing",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("mining").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "mining",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("farming").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "farming",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("alchemy").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "alchemy",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("experience").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "experience",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))))
                    // Remove values
                    .then(CommandManager.literal("remove").then(CommandManager.literal("level").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "level",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("points").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "points",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("health").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "health",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("strength").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "strength",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("agility").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "agility",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("defense").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "defense",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("stamina").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "stamina",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("luck").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "luck",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("archery").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "archery",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("trade").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "trade",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("smithing").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "smithing",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("mining").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "mining",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("farming").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "farming",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("alchemy").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "alchemy",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(CommandManager.literal("experience").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "experience",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))))
                    // Set values
                    .then(CommandManager.literal("set").then(CommandManager.literal("level").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "level",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("points").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "points",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("health").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "health",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("strength").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "strength",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("agility").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "agility",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("defense").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "defense",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("stamina").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "stamina",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("luck").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "luck",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("archery").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "archery",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("trade").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "trade",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("smithing").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "smithing",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("mining").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "mining",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("farming").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "farming",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("alchemy").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "alchemy",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(CommandManager.literal("experience").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "experience",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))))
                    // Print values
                    .then(CommandManager.literal("get").then(CommandManager.literal("level").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "level", 0, 3);
                    })).then(CommandManager.literal("all").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "all", 0, 3);
                    })).then(CommandManager.literal("points").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "points", 0, 3);
                    })).then(CommandManager.literal("health").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "health", 0, 3);
                    })).then(CommandManager.literal("strength").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "strength", 0, 3);
                    })).then(CommandManager.literal("agility").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "agility", 0, 3);
                    })).then(CommandManager.literal("defense").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "defense", 0, 3);
                    })).then(CommandManager.literal("stamina").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "stamina", 0, 3);
                    })).then(CommandManager.literal("luck").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "luck", 0, 3);
                    })).then(CommandManager.literal("archery").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "archery", 0, 3);
                    })).then(CommandManager.literal("trade").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "trade", 0, 3);
                    })).then(CommandManager.literal("smithing").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "smithing", 0, 3);
                    })).then(CommandManager.literal("mining").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "mining", 0, 3);
                    })).then(CommandManager.literal("farming").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "farming", 0, 3);
                    })).then(CommandManager.literal("alchemy").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "alchemy", 0, 3);
                    })).then(CommandManager.literal("experience").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "experience", 0, 3);
                    })))));
        });
    }

    // Reference 0:Add, 1:Remove, 2:Set, 3:Print
    private static int executeSkillCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String skill, int i, int reference) {
        Iterator<ServerPlayerEntity> var3 = targets.iterator();

        i = MathHelper.abs(i);
        // loop over players
        while (var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) var3.next();
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager();
            if (skill.equals("experience")) {
                if (reference == 0)
                    ((PlayerSyncAccess) serverPlayerEntity).addLevelExperience(i);
                if (reference == 1) {
                    int currentXP = (int) (playerStatsManager.levelProgress * playerStatsManager.getNextLevelExperience());
                    float oldProgress = playerStatsManager.levelProgress;
                    playerStatsManager.levelProgress = currentXP - i > 0 ? (float) (currentXP - 1) / (float) playerStatsManager.getNextLevelExperience() : 0.0F;
                    playerStatsManager.totalLevelExperience = currentXP - i > 0 ? playerStatsManager.totalLevelExperience - i
                            : playerStatsManager.totalLevelExperience - (int) (oldProgress * playerStatsManager.getNextLevelExperience());
                }
                if (reference == 2) {
                    float oldProgress = playerStatsManager.levelProgress;
                    playerStatsManager.levelProgress = i >= playerStatsManager.getNextLevelExperience() ? 1.0F : (float) i / playerStatsManager.getNextLevelExperience();
                    playerStatsManager.totalLevelExperience = (int) (playerStatsManager.totalLevelExperience - oldProgress * playerStatsManager.getNextLevelExperience()
                            + playerStatsManager.levelProgress * playerStatsManager.getNextLevelExperience());
                }
                if (reference == 3)
                    source.sendFeedback(new TranslatableText("commands.playerstats.printProgress", serverPlayerEntity.getDisplayName(),
                            (int) (playerStatsManager.levelProgress * playerStatsManager.getNextLevelExperience()), playerStatsManager.getNextLevelExperience()), true);
            } else {
                int playerSkillLevel = playerStatsManager.getLevel(skill);
                if (reference == 0)
                    playerSkillLevel += i;
                if (reference == 1)
                    playerSkillLevel = playerSkillLevel - i > 0 ? playerSkillLevel - i : 0;
                if (reference == 2)
                    playerSkillLevel = i;
                if (reference == 3) {
                    if (skill.equals("all"))
                        for (int u = 0; u < skillStrings().size(); u++) {
                            skill = skillStrings().get(u);
                            if (skill.equals("experience"))
                                source.sendFeedback(new TranslatableText("commands.playerstats.printProgress", serverPlayerEntity.getDisplayName(),
                                        (int) (playerStatsManager.levelProgress * playerStatsManager.getNextLevelExperience()), playerStatsManager.getNextLevelExperience()), true);
                            else
                                source.sendFeedback(new TranslatableText("commands.playerstats.printLevel", serverPlayerEntity.getDisplayName(),
                                        StringUtils.capitalize(skill) + (skill.equals("level") || skill.equals("points") ? ":" : " Level:"), playerStatsManager.getLevel(skill)), true);
                        }
                    else
                        source.sendFeedback(new TranslatableText("commands.playerstats.printLevel", serverPlayerEntity.getDisplayName(),
                                StringUtils.capitalize(skill) + (skill.equals("level") || skill.equals("points") ? ":" : " Level:"), playerSkillLevel), true);

                    continue;
                }
                playerStatsManager.setLevel(skill, playerSkillLevel);

                if (skill.equals("health")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                            .setBaseValue(ConfigInit.CONFIG.healthBase + (double) playerSkillLevel * ConfigInit.CONFIG.healthBonus);
                    serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
                } else if (skill.equals("strength")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                            .setBaseValue(ConfigInit.CONFIG.attackBase + (double) playerSkillLevel * ConfigInit.CONFIG.attackBonus);
                } else if (skill.equals("agility")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                            .setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerSkillLevel * ConfigInit.CONFIG.movementBonus);
                } else if (skill.equals("defense")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(ConfigInit.CONFIG.defenseBase + (double) playerSkillLevel * ConfigInit.CONFIG.defenseBonus);
                } else if (skill.equals("luck")) {
                    serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(ConfigInit.CONFIG.luckBase + (double) playerSkillLevel * ConfigInit.CONFIG.luckBonus);
                }
                if (skill.equals("level")) {
                    final int level = playerSkillLevel;
                    serverPlayerEntity.getScoreboard().forEachScore(CriteriaInit.LEVELZ, serverPlayerEntity.getEntityName(), score -> score.setScore(level));
                    serverPlayerEntity.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
                }
            }
            PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, serverPlayerEntity);

            if (reference != 3)
                source.sendFeedback(new TranslatableText("commands.playerstats.changed", serverPlayerEntity.getDisplayName()), true);
        }

        return targets.size();
    }

    private static int executeInfoMaterial(ServerCommandSource source) throws CommandSyntaxException {
        if (source.getPlayer() != null && !source.getPlayer().getMainHandStack().isEmpty()) {
            Item item = source.getPlayer().getMainHandStack().getItem();
            Text text = null;

            if (item instanceof ArmorItem)
                text = Text.of("Material id: \"" + ((ArmorItem) item).getMaterial().getName().toLowerCase() + "\"");

            if (item instanceof ToolItem)
                text = Text.of("Material id: \"" + ((ToolItem) item).getMaterial().toString().toLowerCase() + "\"");

            source.getPlayer().sendMessage(text != null ? text : Text.of(item.getName().getString() + " does not have a material id"), true);
        }

        return 1;
    }

    private static List<String> skillStrings() {
        return List.of("agility", "alchemy", "archery", "defense", "farming", "health", "luck", "mining", "smithing", "stamina", "strength", "trade", "level", "points", "experience");
    }

}