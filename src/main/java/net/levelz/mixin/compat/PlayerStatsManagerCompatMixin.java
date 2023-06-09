package net.levelz.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerStatsManager.class)
public class PlayerStatsManagerCompatMixin {

    @Inject(method = "playerLevelisHighEnough", at = @At("HEAD"), cancellable = true)
    private static void playerLevelisHighEnoughMixin(PlayerEntity playerEntity, List<Object> list, String string, boolean creativeRequired, CallbackInfoReturnable<Boolean> info) {

        if (playerEntity.getClass().getName().contains("deployer.DeployerFakePlayer") || playerEntity.getClass().getName().contains("core.TurtlePlayer")) {
            info.setReturnValue(true);
        }
    }
}
