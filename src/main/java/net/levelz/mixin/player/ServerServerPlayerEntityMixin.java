package net.levelz.mixin.player;

import com.mojang.authlib.GameProfile;
import net.levelz.access.LevelManagerAccess;
import net.levelz.access.ServerPlayerSyncAccess;
import net.levelz.init.CriteriaInit;
import net.levelz.level.LevelManager;
import net.levelz.util.PacketHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerSyncAccess {

    @Unique
    private final LevelManager levelManager = ((LevelManagerAccess) this).getLevelManager();
    @Unique
    private int syncedLevelExperience = -99999999;

//    private boolean syncTeleportStats = false;
//    private int tinySyncTicker = 0;

    public ServerServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Override
    public void addLevelExperience(int experience) {
        if (!levelManager.isMaxLevel()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;
            levelManager.setLevelProgress(levelManager.getLevelProgress() + Math.max((float) experience / levelManager.getNextLevelExperience(), 0));
            levelManager.setTotalLevelExperience(MathHelper.clamp(levelManager.getTotalLevelExperience() + experience, 0, Integer.MAX_VALUE));

            while (levelManager.getLevelProgress() >= 1.0F && !levelManager.isMaxLevel()) {
                levelManager.setLevelProgress((levelManager.getLevelProgress() - 1.0F) * (float) levelManager.getNextLevelExperience());
                levelManager.addExperienceLevels(1);
                levelManager.setLevelProgress(levelManager.getLevelProgress() / levelManager.getNextLevelExperience());

                PacketHelper.updateLevels(serverPlayerEntity);
                CriteriaInit.LEVEL_UP.trigger(serverPlayerEntity);
                serverPlayerEntity.getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
                serverPlayerEntity.getScoreboard().forEachScore(CriteriaInit.LEVELZ, serverPlayerEntity, ScoreAccess::incrementScore);
                if (levelManager.getOverallLevel() > 0) {
                    serverPlayerEntity.getWorld().playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, serverPlayerEntity.getSoundCategory(), 1.0F, 1.0F);
                }
            }
        }
        this.syncedLevelExperience = -1;
    }



    @Inject(method = "playerTick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;totalExperience:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void playerTickMixin(CallbackInfo info) {
        if (levelManager.getTotalLevelExperience() != this.syncedLevelExperience) {
            this.syncedLevelExperience = levelManager.getTotalLevelExperience();

//            PlayerStatsServerPacket.writeS2CXPPacket(levelManager, ((ServerPlayerEntity) (Object) this));
            PacketHelper.updateLevels((ServerPlayerEntity) (Object) this);
//            PacketHelper.
//            if (this.syncTeleportStats) {
//                PlayerStatsServerPacket.writeS2CSkillPacket(levelManager, (ServerPlayerEntity) (Object) this);
//                this.syncTeleportStats = false;
//            }
        }
//        if (this.tinySyncTicker > 0) {
//            this.tinySyncTicker--;
//            if (this.tinySyncTicker % 20 == 0) {
//                syncStats(false);
//            }
//        }

    }

//    @Inject(method = "onSpawn", at = @At(value = "TAIL"))
//    private void onSpawnMixin(CallbackInfo info) {
//        PlayerStatsServerPacket.writeS2CSkillPacket(levelManager, (ServerPlayerEntity) (Object) this);
//    }
//
//    @Inject(method = "copyFrom", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;syncedExperience:I", ordinal = 0))
//    private void copyFromMixin(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
//        syncStats(false);
//    }

    // tinySyncer is necessary due to client player issues
    // client player isn't readily loaded when S2C Packets roll out for any reason
    @Override
    public void syncStats(boolean syncDelay) {
//        this.syncTeleportStats = true;
//        this.syncedLevelExperience = -1;
//        if (syncDelay)
//            this.tinySyncTicker = 40;
    }

}
