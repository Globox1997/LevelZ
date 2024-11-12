package net.levelz.mixin.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.LevelManagerAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow
    @Mutable
    @Final
    private MinecraftClient client;

    @ModifyConstant(method = "renderExperienceLevel", constant = @Constant(intValue = 8453920), require = 0)
    private int modifyExperienceNumberColor(int original) {
        if (((LevelManagerAccess) client.player).getLevelManager().hasAvailableLevel()) {
            return 1507303;
        } else {
            return original;
        }
    }

}
