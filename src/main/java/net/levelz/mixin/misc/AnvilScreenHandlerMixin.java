package net.levelz.mixin.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow
    @Final
    private Property levelCost;

    private int smithingLevel = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player).getLevel("smithing");

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "Lnet/minecraft/screen/AnvilScreenHandler;updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", shift = At.Shift.AFTER))
    public void updateResultMixin(CallbackInfo info) {
        int levelCost = this.levelCost.get() * (1 - smithingLevel / 40);
        if (levelCost > 30 && smithingLevel >= (int) ConfigInit.CONFIG.maxLevel * 0.75F) {
            this.levelCost.set(30);
        } else
            this.levelCost.set(levelCost);
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getLevelCost", at = @At(value = "HEAD"), cancellable = true)
    public void getLevelCostMixin(CallbackInfoReturnable<Integer> info) {
        if (this.levelCost.get() > 30 && smithingLevel >= (int) ConfigInit.CONFIG.maxLevel * 0.75F) {
            info.setReturnValue(30);
        }
    }
}