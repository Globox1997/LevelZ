package net.levelz.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerDropAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;

@Mixin(AbstractMinecartContainer.class)
public class StorageMinecartEntityMixin {

    @Inject(method = "interact", at = @At("HEAD"))
    private void interactMixin(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
        ((PlayerDropAccess) player).resetKilledMobStat();
    }
}
