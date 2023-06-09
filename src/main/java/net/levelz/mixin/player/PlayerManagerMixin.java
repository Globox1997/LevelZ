package net.levelz.mixin.player;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.CriteriaInit;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

import com.mojang.authlib.GameProfile;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;onPlayerConnected(Lnet/minecraft/server/network/ServerPlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info, GameProfile gameProfile, UserCache userCache, String string,
            NbtCompound nbtCompound) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
        boolean isFirstTimeJoin = nbtCompound == null;
        if (isFirstTimeJoin && server != null && (server.getSaveProperties().getGeneratorOptions().hasBonusChest() || ConfigInit.CONFIG.enableStartPoints)) {
            playerStatsManager.setSkillPoints(ConfigInit.CONFIG.startPoints);
        }
        PlayerStatsServerPacket.writeS2CListPacket(player);
        if (isFirstTimeJoin) {
            player.setHealth(player.getMaxHealth());
        }
        // Sync strength on client cause out of any reason it doesn't work naturally as with respawnPlayer
        PlayerStatsServerPacket.writeS2CStrengthPacket(player);
    }

    @Inject(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;onPlayerRespawned(Lnet/minecraft/server/network/ServerPlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void respawnPlayerMixin(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> info, BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld,
            Optional<Object> optional, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity) {
        if (alive || !ConfigInit.CONFIG.hardMode) {
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
            PlayerStatsManager serverPlayerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager();
            // Set on client
            PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, serverPlayerEntity);
            // Set on server
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH));
            serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR));
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_LUCK));
            // Sync strength on client cause out of any reason it doesn't work naturally
            PlayerStatsServerPacket.writeS2CStrengthPacket(serverPlayerEntity);
            // Check if Client will set to 0 after death
            boolean keepInventory = serverWorld.getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
            serverPlayerStatsManager.setLevelProgress(keepInventory ? playerStatsManager.getLevelProgress() : ConfigInit.CONFIG.resetCurrentXP ? 0 : playerStatsManager.getLevelProgress());
            serverPlayerStatsManager
                    .setTotalLevelExperience(keepInventory ? playerStatsManager.getTotalLevelExperience() : ConfigInit.CONFIG.resetCurrentXP ? 0 : playerStatsManager.getTotalLevelExperience());
            // Level
            serverPlayerStatsManager.setOverallLevel(playerStatsManager.getOverallLevel());
            serverPlayerStatsManager.setSkillPoints(playerStatsManager.getSkillPoints());
            // Skill
            for (Skill skill : Skill.values()) {
                serverPlayerStatsManager.setSkillLevel(skill, playerStatsManager.getSkillLevel(skill));
            }
        }
        if (ConfigInit.CONFIG.hardMode) {
            serverPlayerEntity.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
            serverPlayerEntity.getScoreboard().forEachScore(CriteriaInit.LEVELZ, serverPlayerEntity.getEntityName(), ScoreboardPlayerScore::clearScore);
        }
    }

}