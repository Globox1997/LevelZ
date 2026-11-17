package net.levelz.init;

import net.levelz.access.LevelManagerAccess;
import net.levelz.data.DatapackErrors;
import net.levelz.level.LevelManager;
import net.levelz.level.PlayerSkill;
import net.levelz.level.Skill;
import net.levelz.mixin.entity.EntityAccessor;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Map;

public class EventInit {

    public static void onPlayerJoin(ServerPlayer player) {
        for (Skill skill : LevelManager.SKILLS.values()) {
            LevelHelper.updateSkill(player, skill);
        }
        PacketHelper.syncEnchantments(player);
        PacketHelper.updateSkills(player);
        PacketHelper.updatePlayerSkills(player, null);
        PacketHelper.updateRestrictions(player);
        DatapackErrors.notify(player);
    }

    public static void onPlayerChangeLevel(ServerPlayer player) {
        PacketHelper.updatePlayerSkills(player, null);
        PacketHelper.updateLevels(player);
    }

    public static void onPlayerCopy(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (alive) {
            PacketHelper.updatePlayerSkills(newPlayer, oldPlayer);
            PacketHelper.updateLevels(newPlayer);
        }
    }

    public static void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        LevelManager newLevelManager = ((LevelManagerAccess) newPlayer).getLevelManager();

        if (alive) {
            for (Skill skill : LevelManager.SKILLS.values()) {
                LevelHelper.updateSkill(newPlayer, skill);
            }
            return;
        }

        if (ConfigInit.CONFIG.levelRetainPercentage < 100) {
            LevelManager oldLevelManager = ((LevelManagerAccess) oldPlayer).getLevelManager();
            float levelRetainPercentageFloat = ConfigInit.CONFIG.levelRetainPercentage / 100;
            int retainedLevel = (int) (oldLevelManager.getOverallLevel() * levelRetainPercentageFloat);

            int pointsToDistribute = retainedLevel * ConfigInit.CONFIG.pointsPerLevel + ConfigInit.CONFIG.startPoints;

            if (oldLevelManager.getSkillPoints() > 0) {
                int retainingSkillPoints = (int) (oldLevelManager.getSkillPoints() * levelRetainPercentageFloat);
                newLevelManager.setSkillPoints(retainingSkillPoints);
                pointsToDistribute -= retainingSkillPoints;
            }
            for (Map.Entry<Integer, PlayerSkill> entry : oldLevelManager.getPlayerSkills().entrySet()) {
                int retainingLevel = (int) (entry.getValue().getLevel() * levelRetainPercentageFloat);
                newLevelManager.setSkillLevel(entry.getKey(), retainingLevel);
                pointsToDistribute -= retainingLevel;
                if (pointsToDistribute < 0) {
                    break;
                }
            }
            if (pointsToDistribute > 0) {
                for (Map.Entry<Integer, PlayerSkill> entry : oldLevelManager.getPlayerSkills().entrySet()) {
                    if (newLevelManager.getSkillLevel(entry.getKey()) < entry.getValue().getLevel()) {
                        int levelDifference = entry.getValue().getLevel() - newLevelManager.getSkillLevel(entry.getKey());
                        if (levelDifference < pointsToDistribute) {
                            newLevelManager.setSkillLevel(entry.getKey(), newLevelManager.getSkillLevel(entry.getKey()) + levelDifference);
                            pointsToDistribute -= levelDifference;
                        } else {
                            newLevelManager.setSkillLevel(entry.getKey(), newLevelManager.getSkillLevel(entry.getKey()) + pointsToDistribute);
                            pointsToDistribute = 0;
                            break;
                        }
                    }
                }
                if (pointsToDistribute > 0) {
                    newLevelManager.setSkillPoints(newLevelManager.getSkillPoints() + pointsToDistribute);
                }
            }

            PacketHelper.updatePlayerSkills(newPlayer, null);

            newLevelManager.setOverallLevel(retainedLevel);
            PacketHelper.updateLevels(newPlayer);
            newPlayer.level().getServer().getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, newPlayer));
        } else {
            PacketHelper.updatePlayerSkills(newPlayer, oldPlayer);

            if (ConfigInit.CONFIG.resetCurrentXp) {
                float lostProgress = newLevelManager.getLevelProgress() * Mth.clamp(ConfigInit.CONFIG.currentXpLossPercentage, 0.0F, 100.0F) / 100.0F;
                int lostExperience = (int) (lostProgress * newLevelManager.getNextLevelExperience());
                newLevelManager.setLevelProgress(newLevelManager.getLevelProgress() - lostProgress);
                newLevelManager.setTotalLevelExperience(Math.max(newLevelManager.getTotalLevelExperience() - lostExperience, 0));
            }

            PacketHelper.updateLevels(newPlayer);
        }
        for (Skill skill : LevelManager.SKILLS.values()) {
            LevelHelper.updateSkill(newPlayer, skill);
        }
    }

    public static InteractionResult onUseItem(Player player, InteractionHand hand) {
        if (!player.isCreative() && !player.isSpectator()) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
            if (!levelManager.hasRequiredStackLevel(player.getItemInHand(hand))) {
                // player.sendMessage(Text.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) +
                // ".tooltip", customList.get(customList.indexOf(string) + 2)).formatted(Formatting.RED), true);
                player.sendOverlayMessage(Component.translatable("restriction.levelz.locked.tooltip").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onUseBlock(Player player, Level world, BlockHitResult result) {
        if (!player.isCreative() && !player.isSpectator()) {
            BlockPos blockPos = result.getBlockPos();
            if (world.mayInteract(player, blockPos)) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                if (!levelManager.hasRequiredBlockLevel(world.getBlockState(blockPos).getBlock())) {
                    player.sendOverlayMessage(Component.translatable("restriction.levelz.locked.tooltip").withStyle(ChatFormatting.RED));
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onUseEntity(Player player, Entity entity) {
        if (!player.isCreative() && !player.isSpectator()) {
            if (!entity.hasControllingPassenger() || !((EntityAccessor) entity).callCanAddPassenger(player)) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                if (!levelManager.hasRequiredEntityLevel(entity.getType())) {
                    player.sendOverlayMessage(Component.translatable("restriction.levelz.locked.tooltip").withStyle(ChatFormatting.RED));
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }

}
