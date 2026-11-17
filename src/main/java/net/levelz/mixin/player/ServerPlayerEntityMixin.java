package net.levelz.mixin.player;

import com.mojang.authlib.GameProfile;
import net.levelz.access.LevelManagerAccess;
import net.levelz.access.ServerPlayerSyncAccess;
import net.levelz.init.CriteriaInit;
import net.levelz.level.LevelManager;
import net.levelz.util.PacketHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.scores.ScoreAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements ServerPlayerSyncAccess {

    @Unique
    private final LevelManager levelManager = ((LevelManagerAccess) this).getLevelManager();
    @Unique
    private int syncedLevelExperience = -99999999;

    public ServerPlayerEntityMixin(Level world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At("HEAD"), cancellable = true)
    private void teleportToMixin(TeleportTransition teleportTarget, CallbackInfoReturnable<Entity> info) {
        if (this.isCreative() || this.isSpectator() || teleportTarget.newLevel() == this.level()) {
            return;
        }
        ResourceKey<Level> dimension = teleportTarget.newLevel().dimension();
        if (!levelManager.hasRequiredDimensionLevel(dimension)) {
            MutableComponent requirements = Component.empty();
            for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredDimensionLevel(dimension).entrySet()) {
                if (levelManager.getSkillLevel(entry.getKey()) < entry.getValue() && LevelManager.SKILLS.containsKey(entry.getKey())) {
                    if (!requirements.getSiblings().isEmpty()) {
                        requirements.append(", ");
                    }
                    requirements.append(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()));
                }
            }
            this.sendOverlayMessage(Component.translatable("restriction.levelz.dimension.locked", requirements).withStyle(ChatFormatting.RED));
            info.setReturnValue(null);
        }
    }

    @Override
    public void addLevelExperience(int experience) {
        if (!levelManager.isMaxLevel()) {
            ServerPlayer serverPlayerEntity = (ServerPlayer) (Object) this;
            levelManager.setLevelProgress(levelManager.getLevelProgress() + Math.max((float) experience / Math.max(levelManager.getNextLevelExperience(), 1), 0));
            levelManager.setTotalLevelExperience(Mth.clamp(levelManager.getTotalLevelExperience() + experience, 0, Integer.MAX_VALUE));

            while (levelManager.getLevelProgress() >= 1.0F && !levelManager.isMaxLevel()) {
                levelManager.setLevelProgress((levelManager.getLevelProgress() - 1.0F) * (float) levelManager.getNextLevelExperience());
                levelManager.addExperienceLevels(1);
                int nextLevelExperience = levelManager.getNextLevelExperience();
                levelManager.setLevelProgress(nextLevelExperience > 0 ? levelManager.getLevelProgress() / nextLevelExperience : 0.0F);

                PacketHelper.updateLevels(serverPlayerEntity);
                CriteriaInit.LEVEL_UP.trigger(serverPlayerEntity);
                serverPlayerEntity.level().getServer().getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
                serverPlayerEntity.level().getScoreboard().forAllObjectives(CriteriaInit.LEVELZ, serverPlayerEntity, ScoreAccess::increment);
                if (levelManager.getOverallLevel() > 0) {
                    serverPlayerEntity.level().playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.PLAYER_LEVELUP, serverPlayerEntity.getSoundSource(), 1.0F, 1.0F);
                }
            }
        }
        this.syncedLevelExperience = -1;
    }

    @Inject(method = "doTick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;totalExperience:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void playerTickMixin(CallbackInfo info) {
        if (levelManager.getTotalLevelExperience() != this.syncedLevelExperience) {
            this.syncedLevelExperience = levelManager.getTotalLevelExperience();
            PacketHelper.updateLevels((ServerPlayer) (Object) this);
        }

    }

}
