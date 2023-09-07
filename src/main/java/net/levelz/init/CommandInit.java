package net.levelz.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.data.SkillArgumentType;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CommandInit {

    public static void init() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier("levelz", "skill"), SkillArgumentType.class, ConstantArgumentSerializer.of(SkillArgumentType::skill));

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((literal("info").requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(3))).then(literal("material").executes((commandContext) -> executeInfoMaterial(commandContext.getSource()))));
        });

        final var playerstatArgumentBuilder = (literal("playerstats").requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2)))
            .then(argument("targets", EntityArgumentType.players())
            .then(argument("command", StringArgumentType.string()).suggests((commandContext, suggestionsBuilder) -> {
                suggestionsBuilder.suggest("add");
                suggestionsBuilder.suggest("remove");
                suggestionsBuilder.suggest("set");
                suggestionsBuilder.suggest("get");
                return suggestionsBuilder.buildFuture();
            })
            .then(argument("skill", SkillArgumentType.skill())
            .then(argument("value", IntegerArgumentType.integer(0, ConfigInit.CONFIG.maxLevel))
            .executes((commandContext) -> {
                int reference = switch (commandContext.getArgument("command", String.class)) {
                    case "add" -> 0;
                    case "remove" -> 1;
                    case "set" -> 2;
                    case "get" -> 3;
                    default -> throw new IllegalStateException("Unexpected value: " + commandContext.getArgument("command", String.class));
                };
                if(reference == 3){
                    return executeSkillCommand(
                        commandContext.getSource(),
                        EntityArgumentType.getPlayers(commandContext, "targets"),
                        commandContext.getArgument("skill", String.class),
                        0,
                        reference);
                }
                return executeSkillCommand(
                        commandContext.getSource(),
                        EntityArgumentType.getPlayers(commandContext, "targets"),
                        commandContext.getArgument("skill", String.class),
                        commandContext.getArgument("value", Integer.class),
                    reference);
            }
        )))));

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> dispatcher.register(playerstatArgumentBuilder));
    }

    // Reference 0:Add, 1:Remove, 2:Set, 3:Print
    private static int executeSkillCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String skill, int i, int reference) {
        Iterator<ServerPlayerEntity> playerIterator = targets.iterator();

        i = MathHelper.abs(i);
        // loop over players
        while (playerIterator.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) playerIterator.next();
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager();
            if (skill.equals("experience")) {
                if (reference == 0)
                    ((PlayerSyncAccess) serverPlayerEntity).addLevelExperience(i);
                if (reference == 1) {
                    int currentXP = (int) (playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience());
                    float oldProgress = playerStatsManager.getLevelProgress();
                    playerStatsManager.setLevelProgress(currentXP - i > 0 ? (float) (currentXP - 1) / (float) playerStatsManager.getNextLevelExperience() : 0.0F);
                    playerStatsManager.setTotalLevelExperience(currentXP - i > 0 ? playerStatsManager.getTotalLevelExperience() - i
                            : playerStatsManager.getTotalLevelExperience() - (int) (oldProgress * playerStatsManager.getNextLevelExperience()));
                }
                if (reference == 2) {
                    float oldProgress = playerStatsManager.getLevelProgress();
                    playerStatsManager.setLevelProgress(i >= playerStatsManager.getNextLevelExperience() ? 1.0F : (float) i / playerStatsManager.getNextLevelExperience());
                    playerStatsManager.setTotalLevelExperience((int) (playerStatsManager.getTotalLevelExperience() - oldProgress * playerStatsManager.getNextLevelExperience()
                            + playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience()));
                }
                if (reference == 3) {
                    source.sendFeedback(() -> Text.translatable("commands.playerstats.printProgress", serverPlayerEntity.getDisplayName(),
                            (int) (playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience()), playerStatsManager.getNextLevelExperience()), true);
                }
            } else {
                int playerSkillLevel;
                if (skill.equals("points")) {
                    playerSkillLevel = playerStatsManager.getSkillPoints();
                } else if (skill.equals("level")) {
                    playerSkillLevel = playerStatsManager.getOverallLevel();
                } else {
                    playerSkillLevel = playerStatsManager.getSkillLevel(Skill.valueOf(skill.toUpperCase()));
                }
                if (reference == 0) {
                    playerSkillLevel += i;
                }
                if (reference == 1) {
                    playerSkillLevel = playerSkillLevel - i > 0 ? playerSkillLevel - i : 0;
                }
                if (reference == 2) {
                    playerSkillLevel = i;
                }
                if (reference == 3) {
                    if (skill.equals("all")) {
                        for (int u = 0; u < SkillArgumentType.skillStrings().size(); u++) {
                            skill = SkillArgumentType.skillStrings().get(u);
                            if (skill.equals("experience")) {
                                source.sendFeedback(() -> Text.translatable("commands.playerstats.printProgress", serverPlayerEntity.getDisplayName(),
                                        (int) (playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience()), playerStatsManager.getNextLevelExperience()), true);
                            } else {
                                final String finalSkill = SkillArgumentType.skillStrings().get(u);
                                source.sendFeedback(() -> Text.translatable("commands.playerstats.printLevel", serverPlayerEntity.getDisplayName(),
                                        StringUtils.capitalize(finalSkill) + (finalSkill.equals("level") || finalSkill.equals("points") ? ":" : " Level:"),
                                        finalSkill.equals("level") ? playerStatsManager.getOverallLevel()
                                                : finalSkill.equals("points") ? playerStatsManager.getSkillPoints() : playerStatsManager.getSkillLevel(Skill.valueOf(finalSkill.toUpperCase()))),
                                        true);
                            }
                        }
                    } else {
                        final String finalSkill = skill;
                        final int finalPlayerSkillLevel = playerSkillLevel;
                        source.sendFeedback(() -> Text.translatable("commands.playerstats.printLevel", serverPlayerEntity.getDisplayName(),
                                StringUtils.capitalize(finalSkill) + (finalSkill.equals("level") || finalSkill.equals("points") ? ":" : " Level:"), finalPlayerSkillLevel), true);
                    }
                    continue;
                }
                if (skill.equals("points")) {
                    playerStatsManager.setSkillPoints(playerSkillLevel);
                } else if (skill.equals("level")) {
                    playerStatsManager.setOverallLevel(playerSkillLevel);
                    final int level = playerSkillLevel;
                    serverPlayerEntity.getScoreboard().forEachScore(CriteriaInit.LEVELZ, serverPlayerEntity.getEntityName(), score -> score.setScore(level));
                    serverPlayerEntity.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
                } else {
                    playerStatsManager.setSkillLevel(Skill.valueOf(skill.toUpperCase()), playerSkillLevel);
                }
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
            }
            PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, serverPlayerEntity);

            if (reference != 3) {
                source.sendFeedback(() -> Text.translatable("commands.playerstats.changed", serverPlayerEntity.getDisplayName()), true);
            }
        }

        return targets.size();
    }

    private static int executeInfoMaterial(ServerCommandSource source) {
        if (source.getPlayer() != null && !source.getPlayer().getMainHandStack().isEmpty()) {
            Item item = source.getPlayer().getMainHandStack().getItem();
            Text text = null;

            if (item instanceof ArmorItem) {
                text = Text.of("Material id: \"" + ((ArmorItem) item).getMaterial().getName().toLowerCase() + "\"");
            }
            if (item instanceof ToolItem) {
                text = Text.of("Material id: \"" + ((ToolItem) item).getMaterial().toString().toLowerCase() + "\"");
            }
            source.getPlayer().sendMessage(text != null ? text : Text.of(item.getName().getString() + " does not have a material id"));
        }

        return 1;
    }

}