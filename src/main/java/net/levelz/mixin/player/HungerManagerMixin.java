package net.levelz.mixin.player;

import net.levelz.util.BonusHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class HungerManagerMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V", ordinal = 1))
    private void updateStaminaMixin(Player player, CallbackInfo info) {
        BonusHelper.healthRegenBonus(player);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", shift = Shift.AFTER, ordinal = 0))
    private void updateAbsorptionMixin(Player player, CallbackInfo info) {
        BonusHelper.healthAbsorptionBonus(player);
    }
}