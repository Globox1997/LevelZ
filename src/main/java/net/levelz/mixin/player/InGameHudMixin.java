package net.levelz.mixin.player;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {
}
// Recommend to play with https://www.curseforge.com/minecraft/mc-mods/health-overlay-fabric