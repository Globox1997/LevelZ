package net.levelz.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.levelz.access.LevelManagerAccess;
import net.levelz.access.ServerPlayerSyncAccess;
import net.levelz.data.DatapackErrors;
import net.levelz.data.RestrictionLoader;
import net.levelz.editor.RestrictionEditor;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class CommandInit {

    private static final SuggestionProvider<CommandSourceStack> SKILLS_SUGGESTION_PROVIDER = (context, builder) -> SharedSuggestionProvider.suggest(
            LevelManager.SKILLS.values().stream().map(Skill::getKey), builder);

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((Commands.literal("level").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(Commands.argument("targets", EntityArgument.players())
                    // Add values
                    .then(Commands.literal("add").then(Commands.literal("level").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "level",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(Commands.literal("points").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "points",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(Commands.argument("skillKey", StringArgumentType.string()).suggests(SKILLS_SUGGESTION_PROVIDER).then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), StringArgumentType.getString(commandContext, "skillKey"),
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))).then(Commands.literal("experience").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "experience",
                                IntegerArgumentType.getInteger(commandContext, "level"), 0);
                    }))))
                    // Remove values
                    .then(Commands.literal("remove").then(Commands.literal("level").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "level",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(Commands.literal("points").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "points",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(Commands.argument("skillKey", StringArgumentType.string()).suggests(SKILLS_SUGGESTION_PROVIDER).then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), StringArgumentType.getString(commandContext, "skillKey"),
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))).then(Commands.literal("experience").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "experience",
                                IntegerArgumentType.getInteger(commandContext, "level"), 1);
                    }))))
                    // Set values
                    .then(Commands.literal("set").then(Commands.literal("level").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "level",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(Commands.literal("points").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "points",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(Commands.argument("skillKey", StringArgumentType.string()).suggests(SKILLS_SUGGESTION_PROVIDER).then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), StringArgumentType.getString(commandContext, "skillKey"),
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))).then(Commands.literal("experience").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "experience",
                                IntegerArgumentType.getInteger(commandContext, "level"), 2);
                    }))))
                    // Print values
                    .then(Commands.literal("get").then(Commands.literal("level").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "level", 0, 3);
                    })).then(Commands.literal("all").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "all", 0, 3);
                    })).then(Commands.literal("points").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "points", 0, 3);
                    })).then(Commands.argument("skillKey", StringArgumentType.string()).suggests(SKILLS_SUGGESTION_PROVIDER).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), StringArgumentType.getString(commandContext, "skillKey"), 0, 3);
                    })).then(Commands.literal("experience").executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "experience", 0, 3);
                    })))));
            dispatcher.register(Commands.literal("levelz").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(Commands.literal("editor").executes(commandContext -> {
                        RestrictionEditor.sendEditorData(commandContext.getSource().getPlayerOrException(), true);
                        return 1;
                    }))
                    .then(Commands.literal("restrictions").then(Commands.literal("reload").executes(commandContext -> {
                        RestrictionLoader.reloadRestrictions(commandContext.getSource().getServer());
                        int problems = DatapackErrors.getErrors().size();
                        commandContext.getSource().sendSuccess(() -> Component.translatable("commands.levelz.restrictions.reloaded", problems), true);
                        return 1;
                    }))));
        });
    }

    // Reference 0:Add, 1:Remove, 2:Set, 3:Print
    private static int executeSkillCommand(CommandSourceStack source, Collection<ServerPlayer> targets, String skillKey, int i, int reference) {

        // loop over players
        for (ServerPlayer serverPlayerEntity : targets) {
            LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).getLevelManager();
            if (skillKey.equals("experience")) {
                if (reference == 0) {
                    ((ServerPlayerSyncAccess) serverPlayerEntity).addLevelExperience(i);
                } else if (reference == 1) {
                    int currentXP = (int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience());
                    float oldProgress = levelManager.getLevelProgress();
                    levelManager.setLevelProgress(currentXP - i > 0 ? (float) (currentXP - i) / (float) levelManager.getNextLevelExperience() : 0.0F);
                    levelManager.setTotalLevelExperience(currentXP - i > 0 ? levelManager.getTotalLevelExperience() - i
                            : levelManager.getTotalLevelExperience() - (int) (oldProgress * levelManager.getNextLevelExperience()));
                } else if (reference == 2) {
                    float oldProgress = levelManager.getLevelProgress();
                    levelManager.setLevelProgress(i >= levelManager.getNextLevelExperience() ? 1.0F : (float) i / levelManager.getNextLevelExperience());
                    levelManager.setTotalLevelExperience((int) (levelManager.getTotalLevelExperience() - oldProgress * levelManager.getNextLevelExperience()
                            + levelManager.getLevelProgress() * levelManager.getNextLevelExperience()));
                } else if (reference == 3) {
                    source.sendSuccess(() -> Component.translatable("commands.level.printProgress", serverPlayerEntity.getDisplayName(),
                            (int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience()), levelManager.getNextLevelExperience()), true);
                }
            } else {
                Skill skill = null;
                int playerSkillLevel = 0;
                if (skillKey.equals("points")) {
                    playerSkillLevel = levelManager.getSkillPoints();
                } else if (skillKey.equals("level")) {
                    playerSkillLevel = levelManager.getOverallLevel();
                } else if (!skillKey.equals("all")) {
                    for (Skill overallSkill : LevelManager.SKILLS.values()) {
                        if (overallSkill.getKey().equals(skillKey)) {
                            playerSkillLevel = levelManager.getSkillLevel(overallSkill.getId());
                            skill = overallSkill;
                            break;
                        }
                    }
                    if (skill == null) {
                        source.sendSuccess(() -> Component.translatable("commands.level.failed"), false);
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
                            source.sendSuccess(() -> Component.translatable("commands.level.printLevel", serverPlayerEntity.getDisplayName(),
                                            StringUtils.capitalize(finalSkill) + (finalSkill.equals("level") || finalSkill.equals("points") ? ":" : " Level:"),
                                            finalSkill.equals("level") ? levelManager.getOverallLevel()
                                                    : finalSkill.equals("points") ? levelManager.getSkillPoints() : levelManager.getSkillLevel(overallSkill.getId())),
                                    true);
                        }
                    } else {
                        final String finalSkill = skillKey;
                        final int finalPlayerSkillLevel = playerSkillLevel;
                        source.sendSuccess(() -> Component.translatable("commands.level.printLevel", serverPlayerEntity.getDisplayName(),
                                StringUtils.capitalize(finalSkill) + (finalSkill.equals("level") || finalSkill.equals("points") ? ":" : " Level:"), finalPlayerSkillLevel), true);
                    }
                    continue;
                }
                if (skillKey.equals("points")) {
                    levelManager.setSkillPoints(playerSkillLevel);
                } else if (skillKey.equals("level")) {
                    levelManager.setOverallLevel(playerSkillLevel);
                    final int level = playerSkillLevel;
                    serverPlayerEntity.level().getScoreboard().forAllObjectives(CriteriaInit.LEVELZ, serverPlayerEntity, score -> score.set(level));
                    serverPlayerEntity.level().getServer().getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
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
                source.sendSuccess(() -> Component.translatable("commands.level.changed", serverPlayerEntity.getDisplayName()), true);
            }
        }

        return targets.size();
    }

}