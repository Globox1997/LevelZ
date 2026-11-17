package net.levelz.mixin.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.LevelManagerAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Environment(EnvType.CLIENT)
@Mixin(ContextualBar.class)
public interface InGameHudMixin {

    @ModifyConstant(method = "extractExperienceLevel", constant = @Constant(intValue = -8323296), require = 0)
    private static int modifyExperienceNumberColor(int original) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && ((LevelManagerAccess) minecraft.player).getLevelManager().hasAvailableLevel()) {
            return 0xFF16FFE7;
        } else {
            return original;
        }
    }

}
