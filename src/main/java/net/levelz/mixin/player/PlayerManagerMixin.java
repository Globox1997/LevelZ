package net.levelz.mixin.player;

import com.mojang.authlib.GameProfile;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.util.PacketHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendActivePlayerEffects(Lnet/minecraft/server/level/ServerPlayer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onPlayerConnectMixin(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo info, GameProfile gameProfile, GameProfileCache userCache, String string, Optional<CompoundTag> optional, ResourceKey<Level> registryKey, ServerLevel serverWorld, ServerLevel serverWorld2) {
        if (optional.isEmpty()) {
            if (ConfigInit.CONFIG.startPoints > 0) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                levelManager.setSkillPoints(ConfigInit.CONFIG.startPoints);
                PacketHelper.updateLevels(player);
            }

        }
    }

}
