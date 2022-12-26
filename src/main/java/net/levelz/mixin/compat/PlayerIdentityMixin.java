package net.levelz.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.variant.IdentityType;
import net.levelz.access.PlayerHealthAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerIdentity.class)
public class PlayerIdentityMixin {

    @Inject(method = "updateIdentity", at = @At("HEAD"))
    private static void updateIdentityHealthMixin(ServerPlayerEntity serverPlayerEntity, IdentityType<?> type, LivingEntity entity, CallbackInfoReturnable<Boolean> info) {
        ((PlayerHealthAccess) serverPlayerEntity).setOldHealth(serverPlayerEntity.getHealth());
    }

    @Inject(method = "updateIdentity", at = @At("TAIL"))
    private static void updateIdentityMixin(ServerPlayerEntity serverPlayerEntity, IdentityType<?> type, LivingEntity entity, CallbackInfoReturnable<Boolean> info) {
        serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(
                ConfigInit.CONFIG.healthBase + (double) ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager(serverPlayerEntity).getLevel("health") * ConfigInit.CONFIG.healthBonus);
        serverPlayerEntity.setHealth(((PlayerHealthAccess) serverPlayerEntity).getOldHealth());
    }
}
