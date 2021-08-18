package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow
    private int foodLevel;
    @Shadow
    private float foodSaturationLevel;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V", ordinal = 1))
    private void updateStaminaMixin(PlayerEntity player, CallbackInfo info) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        player.heal((float) playerStatsManager.getLevel("stamina") * ConfigInit.CONFIG.staminaHealthBonus);

    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V", shift = Shift.AFTER, ordinal = 1))
    private void updateAbsorptionMixin(PlayerEntity player, CallbackInfo info) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        if (player.getMaxHealth() <= player.getHealth() && player.getAbsorptionAmount() <= 0.0F && playerStatsManager.getLevel("health") == ConfigInit.CONFIG.maxLevel) {
            player.setAbsorptionAmount(ConfigInit.CONFIG.healthAbsorptionBonus);
        }
    }
}