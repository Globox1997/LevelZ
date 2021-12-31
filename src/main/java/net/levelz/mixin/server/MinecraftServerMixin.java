package net.levelz.mixin.server;

import net.levelz.config.LevelRule;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow public abstract ServerScoreboard getScoreboard();

    @Inject(method = "loadWorld", at = @At(value = "TAIL"))
    private void loadWorld(CallbackInfo ci) {
        LevelRule.registry(this.getScoreboard());
    }
}
