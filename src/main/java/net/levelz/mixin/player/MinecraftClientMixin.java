package net.levelz.mixin.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = MinecraftClient.class, priority = 999)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreakingMixin(boolean breaking, CallbackInfo info) {
        if (restrictHandUsage(true)) {
            info.cancel();
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void doAttackMixin(CallbackInfoReturnable<Boolean> info) {
        if (restrictHandUsage(false)) {
            info.setReturnValue(false);
        }
    }

    private boolean restrictHandUsage(boolean blockBreaking) {
        if (ConfigInit.CONFIG.lockedHandUsage && player != null && !player.isCreative()) {
            Item item = player.getMainHandStack().getItem();
            if (item != null && !item.equals(Items.AIR)) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                if (!levelManager.hasRequiredItemLevel(item)) {
                    player.sendMessage(Text.translatable("item.levelz.locked.tooltip").formatted(Formatting.RED), true);
                    return true;
                }
            }
        }
        return false;
    }
}
