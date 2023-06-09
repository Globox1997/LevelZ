package net.levelz.mixin.player;

import com.mojang.authlib.GameProfile;

import net.levelz.stats.Skill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.CriteriaInit;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements PlayerSyncAccess {

    private PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) this).getPlayerStatsManager();
    private int syncedLevelExperience = -99999999;
    private boolean syncTeleportStats = false;
    private int tinySyncTicker = 0;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void initMixin(MinecraftServer server, ServerWorld world, GameProfile profile, CallbackInfo info) {
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;
        serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .setBaseValue(ConfigInit.CONFIG.healthBase + (double) playerStatsManager.getSkillLevel(Skill.HEALTH) * ConfigInit.CONFIG.healthBonus);
        serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerStatsManager.getSkillLevel(Skill.AGILITY) * ConfigInit.CONFIG.movementBonus);
        serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .setBaseValue(ConfigInit.CONFIG.attackBase + (double) playerStatsManager.getSkillLevel(Skill.STRENGTH) * ConfigInit.CONFIG.attackBonus);
        serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
                .setBaseValue(ConfigInit.CONFIG.defenseBase + (double) playerStatsManager.getSkillLevel(Skill.DEFENSE) * ConfigInit.CONFIG.defenseBonus);
        serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_LUCK)
                .setBaseValue(ConfigInit.CONFIG.luckBase + (double) playerStatsManager.getSkillLevel(Skill.LUCK) * ConfigInit.CONFIG.luckBonus);
        // Init stats - Can't send to client cause network hander is null -> onSpawn packet
    }

    @Override
    public void addLevelExperience(int experience) {
        ServerPlayerEntity playerEntity = (ServerPlayerEntity) (Object) this;
        if (!ConfigInit.CONFIG.useIndependentExp) {
            playerEntity.addExperience(experience);
            return;
        }
        if (!playerStatsManager.isMaxLevel()) {
            playerStatsManager.setLevelProgress(playerStatsManager.getLevelProgress() + Math.max((float) experience / playerStatsManager.getNextLevelExperience(), 0));
            playerStatsManager.setTotalLevelExperience(MathHelper.clamp(playerStatsManager.getTotalLevelExperience() + experience, 0, Integer.MAX_VALUE));
            levelUp(ConfigInit.CONFIG.overallMaxLevel, true, false);
        }
    }

    @Override
    public void levelUp(int levels, boolean deductXp, boolean ignoreMaxLevel) {
        if (levels == 0) {
            levels = Integer.MAX_VALUE;
        }
        ServerPlayerEntity playerEntity = (ServerPlayerEntity) (Object) this;
        for (int i = 0; i < levels; i++) {
            if (!ignoreMaxLevel && playerStatsManager.isMaxLevel()) {
                break;
            }
            if (deductXp) {
                if (playerStatsManager.getLevelProgress() < 1) {
                    break;
                }
                int nextLevelExperience = playerStatsManager.getNextLevelExperience();
                if (!ConfigInit.CONFIG.useIndependentExp) {
                    playerEntity.addExperience(-nextLevelExperience);
                } else {
                    playerStatsManager.setLevelProgress((playerStatsManager.getLevelProgress() - 1.0F) * nextLevelExperience);
                }
            }
            playerStatsManager.addExperienceLevels(1);
            playerStatsManager.setLevelProgress(playerStatsManager.getLevelProgress() / playerStatsManager.getNextLevelExperience());
            PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, playerEntity);
            PlayerStatsManager.onLevelUp(playerEntity, playerStatsManager.getOverallLevel());
            CriteriaInit.LEVEL_UP.trigger(playerEntity, playerStatsManager.getOverallLevel());
            playerEntity.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, playerEntity));
            playerEntity.getScoreboard().forEachScore(CriteriaInit.LEVELZ, this.getEntityName(), ScoreboardPlayerScore::incrementScore);
            if (playerStatsManager.getOverallLevel() > 0) {
                playerEntity.getWorld().playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, playerEntity.getSoundCategory(), 1.0F, 1.0F);
            }
        }
        this.syncedLevelExperience = -1;
    }

    @Inject(method = "playerTick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;totalExperience:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void playerTickMixin(CallbackInfo info) {
        if (playerStatsManager.getTotalLevelExperience() != this.syncedLevelExperience) {
            this.syncedLevelExperience = playerStatsManager.getTotalLevelExperience();
            PlayerStatsServerPacket.writeS2CXPPacket(playerStatsManager, ((ServerPlayerEntity) (Object) this));
            if (this.syncTeleportStats) {
                PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, (ServerPlayerEntity) (Object) this);
                this.syncTeleportStats = false;
            }
        }
        if (this.tinySyncTicker > 0) {
            this.tinySyncTicker--;
            if (this.tinySyncTicker % 20 == 0) {
                syncStats(false);
            }
        }

    }

    @Inject(method = "onSpawn", at = @At(value = "TAIL"))
    private void onSpawnMixin(CallbackInfo info) {
        PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, (ServerPlayerEntity) (Object) this);
    }

    @Inject(method = "copyFrom", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;syncedExperience:I", ordinal = 0))
    private void copyFromMixin(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
        syncStats(false);
    }

    // tinySyncer is necessary due to client player issues
    // client player isn't readily loaded when S2C Packets roll out for any reason
    @Override
    public void syncStats(boolean syncDelay) {
        this.syncTeleportStats = true;
        this.syncedLevelExperience = -1;
        if (syncDelay)
            this.tinySyncTicker = 40;
    }

}
