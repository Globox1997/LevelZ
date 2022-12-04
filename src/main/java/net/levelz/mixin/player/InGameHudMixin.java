package net.levelz.mixin.player;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;

import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.mixin.misc.DrawableHelperAccessor;
import net.levelz.stats.PlayerStatsManager;
import net.fabricmc.api.EnvType;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {

    // Recommend to play with https://www.curseforge.com/minecraft/mc-mods/health-overlay-fabric

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @ModifyConstant(method = "renderExperienceBar", constant = @Constant(intValue = 8453920), require = 0)
    private int modifyExperienceNumberColor(int original) {
        if (((PlayerStatsManagerAccess) client.player).getPlayerStatsManager().hasAvailableLevel())
            return 1507303;
        else
            return original;
    }

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    private void renderCrosshairMixin(MatrixStack matrices, CallbackInfo info) {
        if (this.client.crosshairTarget != null && ConfigInit.CONFIG.showLockedBlockInfo) {
            HitResult hitResult = this.client.crosshairTarget;
            // Add entity tooltip?
            // if (hitResult.getType() == HitResult.Type.ENTITY) {
            // ((EntityHitResult) hitResult).getEntity();
            // // return ((EntityHitResult) hitResult).getEntity() instanceof NamedScreenHandlerFactory;
            // }
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Block block = this.client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getBlock();
                int blockId = Registry.BLOCK.getRawId(block);
                if (PlayerStatsManager.listContainsItemOrBlock(this.client.player, blockId, 1)) {
                    renderTooltip(matrices, Arrays.asList(Text.of(block.getName().getString()), Text.of("Mineable Lv. " + PlayerStatsManager.getUnlockLevel(blockId, 1))), Registry.BLOCK.getId(block),
                            this.scaledWidth / 2 + ConfigInit.CONFIG.lockedBlockInfoPosX, 0 + ConfigInit.CONFIG.lockedBlockInfoPosY);
                }
            }
        }
    }

    private void renderTooltip(MatrixStack matrices, List<Text> text, Identifier identifier, int x, int y) {
        int textWidth = this.client.textRenderer.getWidth(text.get(0)) > this.client.textRenderer.getWidth(text.get(1)) ? this.client.textRenderer.getWidth(text.get(0))
                : this.client.textRenderer.getWidth(text.get(1));
        int l = x - textWidth / 2;

        int m = y + 5;
        int k = textWidth + 20;
        int n = 16;

        matrices.push();

        float f = this.client.getItemRenderer().zOffset;
        this.client.getItemRenderer().zOffset = 400.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        int colorStart = 0xBF191919; // background
        int colorTwo = 0xBF7F0200; // light border
        int colorThree = 0xBF380000; // darker border

        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m - 4, l + k + 3, m - 3, 400, colorStart, colorStart);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m + n + 3, l + k + 3, m + n + 4, 400, colorStart, colorStart);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + k + 3, m + n + 3, 400, colorStart, colorStart);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 4, m - 3, l - 3, m + n + 3, 400, colorStart, colorStart);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l + k + 3, m - 3, l + k + 4, m + n + 3, 400, colorStart, colorStart);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m - 3 + 1, l - 3 + 1, m + n + 3 - 1, 400, colorTwo, colorThree);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l + k + 2, m - 3 + 1, l + k + 3, m + n + 3 - 1, 400, colorTwo, colorThree);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + k + 3, m - 3 + 1, 400, colorTwo, colorTwo);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m + n + 2, l + k + 3, m + n + 3, 400, colorThree, colorThree);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);
        this.client.textRenderer.draw(matrices, text.get(0), x - k / 2 + 30, y + 4, 0xFFFFFF);
        this.client.textRenderer.draw(matrices, text.get(1), x - k / 2 + 30, y + 14, 0xFFFFFF);
        immediate.draw();
        matrices.pop();

        this.client.getItemRenderer().renderInGui(Registry.ITEM.get(identifier).getDefaultStack(), x - k / 2 + 11, y + 5);

        this.client.getItemRenderer().zOffset = f;
    }
}
