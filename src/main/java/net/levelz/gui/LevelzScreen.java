package net.levelz.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.KeyInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class LevelzScreen extends CottonClientScreen {
    public LevelzScreen(GuiDescription description) {
        super(description);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);
        if (this.client.player != null) {
            int scaledWidth = this.client.getWindow().getScaledWidth();
            int scaledHeight = this.client.getWindow().getScaledHeight();
            InventoryScreen.drawEntity(scaledWidth / 2 - 75, scaledHeight / 2 - 40, 30, -28, 0, this.client.player);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) this.client.player).getPlayerStatsManager(this.client.player);
            int m = (int) (playerStatsManager.levelProgress * 131.0F);
            int x = scaledWidth / 2 - 41;
            int n = scaledHeight / 2 - 45;
            LevelzScreen.drawTexture(matrices, x, n, 130, 5, 0F, 64F, 182, 5, 256, 256);
            if (m > 0) {
                this.drawTexture(matrices, x, n, 0, 69, m, 5);
            }
            RenderSystem.disableBlend();

            // PlayerEntity entity = this.client.player;
            // float f = (float) Math.atan((double) (-28F / 40.0F));
            // MatrixStack matrixStack = RenderSystem.getModelViewStack();
            // matrixStack.push();
            // matrixStack.translate((double) scaledWidth / 2 - 75, (double) scaledHeight / 2 - 40, 1050.0D);
            // matrixStack.scale(1.0F, 1.0F, -1.0F);
            // RenderSystem.applyModelViewMatrix();
            // MatrixStack matrixStack2 = new MatrixStack();
            // matrixStack2.translate(0.0D, 0.0D, 1000.0D);
            // matrixStack2.scale((float) 30, (float) 30, (float) 30);
            // Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
            // Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(0F);
            // quaternion.hamiltonProduct(quaternion2);
            // matrixStack2.multiply(quaternion);
            // entity.setBodyYaw(180.0F + f * 20.0F);
            // entity.setYaw(180.0F + f * 40.0F);
            // entity.headYaw = entity.getYaw();
            // entity.prevHeadYaw = entity.getYaw();

            // DiffuseLighting.method_34742();
            // EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            // quaternion2.conjugate();
            // entityRenderDispatcher.setRotation(quaternion2);
            // entityRenderDispatcher.setRenderShadows(false);
            // VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            // RenderSystem.runAsFancy(() -> {
            // entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack2, immediate, 15728880);
            // });
            // immediate.draw();
            // entityRenderDispatcher.setRenderShadows(true);
            // matrixStack.pop();
            // RenderSystem.applyModelViewMatrix();
            // DiffuseLighting.enableGuiDepthLighting();

        }
    }

    @Override
    public boolean keyPressed(int ch, int keyCode, int modifiers) {
        if (KeyInit.screenKey.matchesKey(ch, keyCode) || client.options.keyInventory.matchesKey(ch, keyCode)) {
            this.onClose();
            return true;
        } else
            return super.keyPressed(ch, keyCode, modifiers);

    }

}