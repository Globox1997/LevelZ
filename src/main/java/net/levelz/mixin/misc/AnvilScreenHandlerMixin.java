package net.levelz.mixin.misc;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow
    @Final
    private Property levelCost;

    private int smithingLevel = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player).getLevel("smithing");

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "Lnet/minecraft/screen/AnvilScreenHandler;updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 4))
    private void updateResultMixin(CallbackInfo info) {
        if (this.levelCost.get() > 1) {
            int levelCost = (int) (this.levelCost.get() * (1F - smithingLevel * ConfigInit.CONFIG.smithingCostBonus));
            if (levelCost > 30 && smithingLevel >= ConfigInit.CONFIG.maxLevel) {
                this.levelCost.set(30);
            } else
                this.levelCost.set(levelCost);
        }
    }

    @Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V"), require = 0)
    private void onTakeOutputMixin(PlayerEntity playerEntity, int levelCost) {
        if (!(smithingLevel >= ConfigInit.CONFIG.maxLevel) || !(ConfigInit.CONFIG.smithingAnvilChance > playerEntity.world.random.nextFloat()))
            playerEntity.addExperienceLevels(-this.levelCost.get());
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getLevelCost", at = @At(value = "HEAD"), cancellable = true)
    public void getLevelCostMixin(CallbackInfoReturnable<Integer> info) {
        if (this.levelCost.get() > 30 && smithingLevel >= ConfigInit.CONFIG.maxLevel) {
            info.setReturnValue(30);
        }
    }
}