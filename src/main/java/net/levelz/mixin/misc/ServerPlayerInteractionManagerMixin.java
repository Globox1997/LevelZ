package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.network.ServerPlayerInteractionManager;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    // Instead of checking the unlocked block list every tick while breaking, here it could get checked one time at the start of mining

    // @Inject(method = "processBlockBreakingAction", at = @At(value = "HEAD"))
    // private void processBlockBreakingActionMixin(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo info) {
    // System.out.println("S:" + System.nanoTime());
    // }

}
