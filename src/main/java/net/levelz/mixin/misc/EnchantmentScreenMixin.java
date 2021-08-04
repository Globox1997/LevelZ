package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;

@Environment(EnvType.CLIENT)
@Mixin(EnchantmentScreen.class)
public class EnchantmentScreenMixin {

}
