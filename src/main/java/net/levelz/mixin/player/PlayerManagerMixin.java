package net.levelz.mixin.player;

import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.util.PacketHelper;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendActivePlayerEffects(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void onPlayerConnectMixin(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo info) {
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
        if (!levelManager.hasLoadedData()) {
            if (ConfigInit.CONFIG.startPoints > 0) {
                levelManager.setSkillPoints(ConfigInit.CONFIG.startPoints);
                PacketHelper.updateLevels(player);
            }
        }
    }
}
