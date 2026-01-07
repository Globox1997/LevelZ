package net.levelz.init;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.mixin.entity.EntityAccessor;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class EventInit {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            for (Skill skill : LevelManager.SKILLS.values()) {
                LevelHelper.updateSkill(handler.getPlayer(), skill);
            }
            PacketHelper.syncEnchantments(handler.getPlayer());
            PacketHelper.updateSkills(handler.getPlayer());
            PacketHelper.updatePlayerSkills(handler.getPlayer(), null);
            PacketHelper.updateRestrictions(handler.getPlayer());
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            PacketHelper.updatePlayerSkills(player, null);
            PacketHelper.updateLevels(player);
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (alive) {
                PacketHelper.updatePlayerSkills(newPlayer, oldPlayer);
                PacketHelper.updateLevels(newPlayer);
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            LevelManager newLevelManager = ((LevelManagerAccess) newPlayer).getLevelManager();
            if (ConfigInit.CONFIG.levelRetainPercentage < 100) {
                LevelManager oldLevelManager = ((LevelManagerAccess) oldPlayer).getLevelManager();
                float levelRetainPercentageFloat = ConfigInit.CONFIG.levelRetainPercentage / 100;
                int retainedLevel = (int) (oldLevelManager.getOverallLevel() * levelRetainPercentageFloat);

                int usedSkillPoints = oldLevelManager.getOverallLevel() * ConfigInit.CONFIG.pointsPerLevel + ConfigInit.CONFIG.startPoints - oldLevelManager.getSkillPoints();
                if(!ConfigInit.CONFIG.levelRefundSkillPoints && usedSkillPoints > oldLevelManager.getSkillPoints()) {
                    int lossUnusedSkillPointsAmount = oldLevelManager.getSkillPoints() - ConfigInit.CONFIG.startPoints;
                    newLevelManager.setSkillPoints(ConfigInit.CONFIG.startPoints);

                    List<Integer> skillsList = new ArrayList<>(oldLevelManager.getPlayerSkills().keySet());
                    int lossSkillsAmount = (oldLevelManager.getOverallLevel() - retainedLevel) * ConfigInit.CONFIG.pointsPerLevel - lossUnusedSkillPointsAmount;
                    while (lossSkillsAmount > 0 && !skillsList.isEmpty()) {
                        int skillIdPos = (int) (Math.random() * skillsList.size());
                        int skillId = skillsList.get(skillIdPos);
                        if (oldLevelManager.getSkillLevel(skillId) == 0) {
                            skillsList.remove(skillIdPos);
                            continue;
                        }
                        oldLevelManager.setSkillLevel(skillId, oldLevelManager.getSkillLevel(skillId) - 1);
                        lossSkillsAmount--;
                    }

                    List<Integer> skillsList2 = new ArrayList<>(oldLevelManager.getPlayerSkills().keySet());
                    for (int skillId : skillsList2) {
                        newLevelManager.setSkillLevel(skillId, oldLevelManager.getSkillLevel(skillId));
                    }
                    PacketHelper.updatePlayerSkills(newPlayer, null);
                } else {
                    newPlayer.getScoreboard().forEachScore(CriteriaInit.LEVELZ, newPlayer, ScoreAccess::resetScore);

                    newLevelManager.setSkillPoints(ConfigInit.CONFIG.startPoints + retainedLevel * ConfigInit.CONFIG.pointsPerLevel);
                }
                newLevelManager.setOverallLevel(retainedLevel);
                PacketHelper.updateLevels(newPlayer);
                newPlayer.getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, newPlayer));
            } else {
                PacketHelper.updatePlayerSkills(newPlayer, oldPlayer);

                if (ConfigInit.CONFIG.resetCurrentXp) {
                    newLevelManager.setLevelProgress(0);
                    newLevelManager.setTotalLevelExperience(0);
                }

                PacketHelper.updateLevels(newPlayer);
                for (Skill skill : LevelManager.SKILLS.values()) {
                    LevelHelper.updateSkill(newPlayer, skill);
                }
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                if (!levelManager.hasRequiredItemLevel(player.getStackInHand(hand).getItem())) {
                    // player.sendMessage(Text.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) +
                    // ".tooltip", customList.get(customList.indexOf(string) + 2)).formatted(Formatting.RED), true);
                    player.sendMessage(Text.translatable("restriction.levelz.locked.tooltip").formatted(Formatting.RED), true);
                    return TypedActionResult.fail(player.getStackInHand(hand));
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        UseBlockCallback.EVENT.register((player, world, hand, result) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                BlockPos blockPos = result.getBlockPos();
                if (world.canPlayerModifyAt(player, blockPos)) {
                    LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                    if (!levelManager.hasRequiredBlockLevel(world.getBlockState(blockPos).getBlock())) {
                        player.sendMessage(Text.translatable("restriction.levelz.locked.tooltip").formatted(Formatting.RED), true);
                        return ActionResult.success(false);
                    }
                }
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                if (!entity.hasControllingPassenger() || !((EntityAccessor) entity).callCanAddPassenger(player)) {
                    LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                    if (!levelManager.hasRequiredEntityLevel(entity.getType())) {
                        player.sendMessage(Text.translatable("restriction.levelz.locked.tooltip").formatted(Formatting.RED), true);
                        return ActionResult.success(false);
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

}
