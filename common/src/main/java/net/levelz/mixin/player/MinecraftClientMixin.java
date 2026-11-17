package net.levelz.mixin.player;

import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, priority = 999)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreakingMixin(boolean breaking, CallbackInfo info) {
        if (restrictHandUsage(true)) {
            info.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void doAttackMixin(CallbackInfoReturnable<Boolean> info) {
        if (restrictHandUsage(false)) {
            info.setReturnValue(false);
        }
    }

    @Unique
    private boolean restrictHandUsage(boolean blockBreaking) {
        if (ConfigInit.CONFIG.lockedHandUsage && player != null && !player.isCreative()) {
            Item item = player.getMainHandItem().getItem();
            if (item != null && !item.equals(Items.AIR)) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                if (!levelManager.hasRequiredStackLevel(player.getMainHandItem())) {
                    player.sendOverlayMessage(Component.translatable("item.levelz.locked.tooltip").withStyle(ChatFormatting.RED));
                    return true;
                }
            }
        }
        return false;
    }
}
