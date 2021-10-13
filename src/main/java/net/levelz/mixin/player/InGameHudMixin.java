package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {
    // Recommend to play with https://www.curseforge.com/minecraft/mc-mods/health-overlay-fabric

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyConstant(method = "renderExperienceBar", constant = @Constant(intValue = 8453920), require = 0)
    private int modifyExperienceNumberColor(int original) {
        if (((PlayerStatsManagerAccess) client.player).getPlayerStatsManager(client.player).getLevel("points") > 0)
            return 1507303;
        else
            return original;
    }
}
