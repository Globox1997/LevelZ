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
            if (ConfigInit.CONFIG.hardMode) {
                newPlayer.getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, newPlayer));
                newPlayer.getScoreboard().forEachScore(CriteriaInit.LEVELZ, newPlayer, ScoreAccess::resetScore);
            } else {
                PacketHelper.updatePlayerSkills(newPlayer, oldPlayer);

                if (ConfigInit.CONFIG.resetCurrentXp) {
                    LevelManager levelManager = ((LevelManagerAccess) newPlayer).getLevelManager();
                    levelManager.setLevelProgress(0);
                    levelManager.setTotalLevelExperience(0);
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
