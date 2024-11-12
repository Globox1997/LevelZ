package net.levelz.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.levelz.access.LevelManagerAccess;
import net.levelz.access.ServerPlayerSyncAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class CommandInit {

    private static final SuggestionProvider<ServerCommandSource> SKILLS_SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(
            LevelManager.SKILLS.values().stream().map(Skill::getKey), builder);

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("level").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(2);
            })).then(CommandManager.argument("targets", EntityArgumentType.players())
                    // Add values
                    .then(CommandManager.literal("add").then(CommandManager.literal("level").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "level",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.literal("points").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "points",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(CommandManager.argument("skillKey", StringArgumentType.string()).suggests(SKILLS_SUGGESTION_PROVIDER).then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), StringArgumentType.getString(commandContext, "skillKey"),
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
                    }))).then(CommandManager.argument("skillKey", StringArgumentType.string()).suggests(SKILLS_SUGGESTION_PROVIDER).then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), StringArgumentType.getString(commandContext, "skillKey"),
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
                    }))).then(CommandManager.argument("skillKey", StringArgumentType.string()).suggests(SKILLS_SUGGESTION_PROVIDER).then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), StringArgumentType.getString(commandContext, "skillKey"),
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
                    })).then(CommandManager.argument("skillKey", StringArgumentType.string()).suggests(SKILLS_SUGGESTION_PROVIDER).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), StringArgumentType.getString(commandContext, "skillKey"), 0, 3);
                    })).then(CommandManager.literal("experience").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "experience", 0, 3);
                    })))));
        });
    }

    // Reference 0:Add, 1:Remove, 2:Set, 3:Print
    private static int executeSkillCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String skillKey, int i, int reference) {

        // loop over players
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).getLevelManager();
            if (skillKey.equals("experience")) {
                if (reference == 0) {
                    ((ServerPlayerSyncAccess) serverPlayerEntity).addLevelExperience(i);
                } else if (reference == 1) {
                    int currentXP = (int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience());
                    float oldProgress = levelManager.getLevelProgress();
                    levelManager.setLevelProgress(currentXP - i > 0 ? (float) (currentXP - 1) / (float) levelManager.getNextLevelExperience() : 0.0F);
                    levelManager.setTotalLevelExperience(currentXP - i > 0 ? levelManager.getTotalLevelExperience() - i
                            : levelManager.getTotalLevelExperience() - (int) (oldProgress * levelManager.getNextLevelExperience()));
                } else if (reference == 2) {
                    float oldProgress = levelManager.getLevelProgress();
                    levelManager.setLevelProgress(i >= levelManager.getNextLevelExperience() ? 1.0F : (float) i / levelManager.getNextLevelExperience());
                    levelManager.setTotalLevelExperience((int) (levelManager.getTotalLevelExperience() - oldProgress * levelManager.getNextLevelExperience()
                            + levelManager.getLevelProgress() * levelManager.getNextLevelExperience()));
                } else if (reference == 3) {
                    source.sendFeedback(() -> Text.translatable("commands.level.printProgress", serverPlayerEntity.getDisplayName(),
                            (int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience()), levelManager.getNextLevelExperience()), true);
                }
            } else {
                Skill skill = null;
                int playerSkillLevel = 0;
                if (skillKey.equals("points")) {
                    playerSkillLevel = levelManager.getSkillPoints();
                } else if (skillKey.equals("level")) {
                    playerSkillLevel = levelManager.getOverallLevel();
                } else {
                    for (Skill overallSkill : LevelManager.SKILLS.values()) {
                        if (overallSkill.getKey().equals(skillKey)) {
                            playerSkillLevel = levelManager.getSkillLevel(overallSkill.getId());
                            skill = overallSkill;
                            break;
                        }
                    }
                    if (skill == null) {
                        source.sendFeedback(() -> Text.translatable("commands.level.failed"), false);
                        return 0;
                    }
                }
                if (reference == 0) {
                    playerSkillLevel += i;
                } else if (reference == 1) {
                    playerSkillLevel = Math.max(playerSkillLevel - i, 0);
                } else if (reference == 2) {
                    playerSkillLevel = i;
                } else if (reference == 3) {
                    if (skillKey.equals("all")) {
                        for (Skill overallSkill : LevelManager.SKILLS.values()) {
                            final String finalSkill = overallSkill.getKey();
                            source.sendFeedback(() -> Text.translatable("commands.level.printLevel", serverPlayerEntity.getDisplayName(),
                                            StringUtils.capitalize(finalSkill) + (finalSkill.equals("level") || finalSkill.equals("points") ? ":" : " Level:"),
                                            finalSkill.equals("level") ? levelManager.getOverallLevel()
                                                    : finalSkill.equals("points") ? levelManager.getSkillPoints() : levelManager.getSkillLevel(overallSkill.getId())),
                                    true);
                        }
                    } else {
                        final String finalSkill = skillKey;
                        final int finalPlayerSkillLevel = playerSkillLevel;
                        source.sendFeedback(() -> Text.translatable("commands.level.printLevel", serverPlayerEntity.getDisplayName(),
                                StringUtils.capitalize(finalSkill) + (finalSkill.equals("level") || finalSkill.equals("points") ? ":" : " Level:"), finalPlayerSkillLevel), true);
                    }
                    continue;
                }
                if (skillKey.equals("points")) {
                    levelManager.setSkillPoints(playerSkillLevel);
                } else if (skillKey.equals("level")) {
                    levelManager.setOverallLevel(playerSkillLevel);
                    final int level = playerSkillLevel;
                    serverPlayerEntity.getScoreboard().forEachScore(CriteriaInit.LEVELZ, serverPlayerEntity, score -> score.setScore(level));
                    serverPlayerEntity.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
                } else {
                    levelManager.setSkillLevel(skill.getId(), playerSkillLevel);
                    if (!skill.getAttributes().isEmpty()) {
                        LevelHelper.updateSkill(serverPlayerEntity, skill);
                    }
                }
            }
            PacketHelper.updateLevels(serverPlayerEntity);
            PacketHelper.updatePlayerSkills(serverPlayerEntity, null);

            if (reference != 3) {
                source.sendFeedback(() -> Text.translatable("commands.level.changed", serverPlayerEntity.getDisplayName()), true);
            }
        }

        return targets.size();
    }

}