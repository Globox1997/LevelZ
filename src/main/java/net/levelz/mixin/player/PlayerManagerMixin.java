package net.levelz.mixin.player;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At(value = "TAIL"))
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        boolean isFirstTimeJoin = loadPlayerData(player) == null;
        if (isFirstTimeJoin && server != null && server.getSaveProperties().getGeneratorOptions().hasBonusChest()) {
            playerStatsManager.setLevel("points", ConfigInit.CONFIG.startPoints);
        }
        PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, player);
        PlayerStatsServerPacket.writeS2CListPacket(player);
        if (isFirstTimeJoin) {
            player.setHealth(player.getMaxHealth());
        }
    }

    @Inject(method = "respawnPlayer", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void respawnPlayerMixin(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> info, BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld,
            Optional<Vec3d> optional2, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity) {
        if (alive || !ConfigInit.CONFIG.hardMode) {
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
            PlayerStatsManager serverPlayerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager(serverPlayerEntity);
            // Set on client
            PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, serverPlayerEntity);
            // Set on server
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH));
            serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR));
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_LUCK));

            // Check if Client will set to 0 after death
            serverPlayerStatsManager.levelProgress = ConfigInit.CONFIG.resetCurrentXP ? 0 : playerStatsManager.levelProgress;
            serverPlayerStatsManager.totalLevelExperience = ConfigInit.CONFIG.resetCurrentXP ? 0 : playerStatsManager.totalLevelExperience;
            // Level
            serverPlayerStatsManager.setLevel("level", playerStatsManager.getLevel("level"));
            serverPlayerStatsManager.setLevel("points", playerStatsManager.getLevel("points"));
            // Skill
            serverPlayerStatsManager.setLevel("health", playerStatsManager.getLevel("health"));
            serverPlayerStatsManager.setLevel("strength", playerStatsManager.getLevel("strength"));
            serverPlayerStatsManager.setLevel("agility", playerStatsManager.getLevel("agility"));
            serverPlayerStatsManager.setLevel("defense", playerStatsManager.getLevel("defense"));
            serverPlayerStatsManager.setLevel("stamina", playerStatsManager.getLevel("stamina"));
            serverPlayerStatsManager.setLevel("luck", playerStatsManager.getLevel("luck"));
            serverPlayerStatsManager.setLevel("archery", playerStatsManager.getLevel("archery"));
            serverPlayerStatsManager.setLevel("trade", playerStatsManager.getLevel("trade"));
            serverPlayerStatsManager.setLevel("smithing", playerStatsManager.getLevel("smithing"));
            serverPlayerStatsManager.setLevel("mining", playerStatsManager.getLevel("mining"));
            serverPlayerStatsManager.setLevel("farming", playerStatsManager.getLevel("farming"));
            serverPlayerStatsManager.setLevel("alchemy", playerStatsManager.getLevel("alchemy"));
        }
    }

    @Shadow
    @Nullable
    public NbtCompound loadPlayerData(ServerPlayerEntity player) {
        return null;
    }
}