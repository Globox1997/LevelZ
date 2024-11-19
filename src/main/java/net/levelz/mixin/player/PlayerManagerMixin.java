package net.levelz.mixin.player;

import com.mojang.authlib.GameProfile;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.util.PacketHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendStatusEffects(Lnet/minecraft/server/network/ServerPlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo info, GameProfile gameProfile, UserCache userCache, String string, Optional<NbtCompound> optional, RegistryKey<World> registryKey, ServerWorld serverWorld, ServerWorld serverWorld2) {
        if (optional.isEmpty()) {
            if (ConfigInit.CONFIG.startPoints > 0) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                levelManager.setSkillPoints(ConfigInit.CONFIG.startPoints);
                PacketHelper.updateLevels(player);
            }

        }
    }

}
