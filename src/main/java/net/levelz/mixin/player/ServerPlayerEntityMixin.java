package net.levelz.mixin.player;

import com.mojang.authlib.GameProfile;
import net.levelz.access.LevelManagerAccess;
import net.levelz.access.ServerPlayerSyncAccess;
import net.levelz.init.CriteriaInit;
import net.levelz.level.LevelManager;
import net.levelz.util.PacketHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerSyncAccess {

    @Unique
    private final LevelManager levelManager = ((LevelManagerAccess) this).getLevelManager();
    @Unique
    private int syncedLevelExperience = -99999999;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "teleportTo", at = @At("HEAD"), cancellable = true)
    private void teleportToMixin(TeleportTarget teleportTarget, CallbackInfoReturnable<Entity> info) {
        if (this.isCreative() || this.isSpectator() || teleportTarget.world() == this.getWorld()) {
            return;
        }
        RegistryKey<World> dimension = teleportTarget.world().getRegistryKey();
        if (!levelManager.hasRequiredDimensionLevel(dimension)) {
            MutableText requirements = Text.empty();
            for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredDimensionLevel(dimension).entrySet()) {
                if (levelManager.getSkillLevel(entry.getKey()) < entry.getValue() && LevelManager.SKILLS.containsKey(entry.getKey())) {
                    if (!requirements.getSiblings().isEmpty()) {
                        requirements.append(", ");
                    }
                    requirements.append(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()));
                }
            }
            this.sendMessage(Text.translatable("restriction.levelz.dimension.locked", requirements).formatted(Formatting.RED), true);
            info.setReturnValue(null);
        }
    }

    @Override
    public void addLevelExperience(int experience) {
        if (!levelManager.isMaxLevel()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;
            levelManager.setLevelProgress(levelManager.getLevelProgress() + Math.max((float) experience / Math.max(levelManager.getNextLevelExperience(), 1), 0));
            levelManager.setTotalLevelExperience(MathHelper.clamp(levelManager.getTotalLevelExperience() + experience, 0, Integer.MAX_VALUE));

            while (levelManager.getLevelProgress() >= 1.0F && !levelManager.isMaxLevel()) {
                levelManager.setLevelProgress((levelManager.getLevelProgress() - 1.0F) * (float) levelManager.getNextLevelExperience());
                levelManager.addExperienceLevels(1);
                int nextLevelExperience = levelManager.getNextLevelExperience();
                levelManager.setLevelProgress(nextLevelExperience > 0 ? levelManager.getLevelProgress() / nextLevelExperience : 0.0F);

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
            PacketHelper.updateLevels((ServerPlayerEntity) (Object) this);
        }

    }

}
