package net.levelz.mixin.misc;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends HandledScreen<EnchantmentScreenHandler> {

    public EnchantmentScreenMixin(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "drawBackground", at = @At(value = "TAIL"))
    protected void drawBackgroundMixin(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo info) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        RenderSystem.setShaderTexture(0, RenderInit.GUI_ICONS);
        if (this.isPointWithinBounds(176, 0, 11, 13, (double) mouseX, (double) mouseY)) {
            this.drawTexture(matrices, i + 176, j, 33, 64, 11, 13);
            List<Text> list = Lists.newArrayList();
            String skill = Language.getInstance().get("spritetip.levelz.%s_skill".formatted(LevelLists.enchantingTableList.get(0)));
            list.add((Text.translatable("container.levelz.enchanting_tier", 1, skill, LevelLists.enchantingTableList.get(4))).formatted(Formatting.WHITE));
            list.add((Text.translatable("container.levelz.enchanting_tier", 2, skill, LevelLists.enchantingTableList.get(5))).formatted(Formatting.WHITE));
            list.add((Text.translatable("container.levelz.enchanting_tier", 3, skill, LevelLists.enchantingTableList.get(6))).formatted(Formatting.WHITE));
            this.renderTooltip(matrices, list, mouseX, mouseY);
        } else
            this.drawTexture(matrices, i + 176, j, 22, 64, 11, 13);
    }
}
