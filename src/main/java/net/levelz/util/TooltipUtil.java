package net.levelz.util;

import java.util.Arrays;
import java.util.List;

import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class TooltipUtil {

    // Recommend to play with https://www.curseforge.com/minecraft/mc-mods/health-overlay-fabric

    public static void renderTooltip(MinecraftClient client, DrawContext context) {
        if (client.crosshairTarget != null && ConfigInit.CONFIG.showLockedBlockInfo) {
            HitResult hitResult = client.crosshairTarget;
            // Add entity tooltip?
            // if (hitResult.getType() == HitResult.Type.ENTITY) {
            // ((EntityHitResult) hitResult).getEntity();
            // // return ((EntityHitResult) hitResult).getEntity() instanceof NamedScreenHandlerFactory;
            // }
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Block block = client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getBlock();
                int blockId = Registries.BLOCK.getRawId(block);
                if (PlayerStatsManager.listContainsItemOrBlock(client.player, blockId, 1)) {
                    renderTooltip(client, context, Arrays.asList(Text.of(block.getName().getString()), Text.of("Mineable Lv. " + PlayerStatsManager.getUnlockLevel(blockId, 1))),
                            Registries.BLOCK.getId(block), context.getScaledWindowWidth() / 2 + ConfigInit.CONFIG.lockedBlockInfoPosX, ConfigInit.CONFIG.lockedBlockInfoPosY);
                }
            }
        }
    }

    private static void renderTooltip(MinecraftClient client, DrawContext context, List<Text> text, Identifier identifier, int x, int y) {
        int textWidth = client.textRenderer.getWidth(text.get(0)) > client.textRenderer.getWidth(text.get(1)) ? client.textRenderer.getWidth(text.get(0)) : client.textRenderer.getWidth(text.get(1));
        int l = x - textWidth / 2 - 3;
        int m = y + 4;
        int k = textWidth + 23;
        int n = 17;

        context.getMatrices().push();

        int colorStart = 0xBF191919; // background
        int colorTwo = 0xBF7F0200; // light border
        int colorThree = 0xBF380000; // darker border

        render(context, l, m, k, n, 400, colorStart, colorTwo, colorThree);

        context.getMatrices().translate(0.0, 0.0, 400.0);

        context.drawText(client.textRenderer, text.get(0), x - k / 2 + 30, y + 4, 0xFFFFFF, false);
        context.drawText(client.textRenderer, text.get(1), x - k / 2 + 30, y + 14, 0xFFFFFF, false);

        context.drawItem(Registries.ITEM.get(identifier).getDefaultStack(), x - k / 2 + 11, y + 5);
        context.getMatrices().pop();
    }

    public static void render(DrawContext context, int x, int y, int width, int height, int z, int background, int borderColorStart, int borderColorEnd) {
        int i = x - 3;
        int j = y - 3;
        int k = width + 3 + 3;
        int l = height + 3 + 3;
        renderHorizontalLine(context, i, j - 1, k, z, background);
        renderHorizontalLine(context, i, j + l, k, z, background);
        renderRectangle(context, i, j, k, l, z, background);
        renderVerticalLine(context, i - 1, j, l, z, background);
        renderVerticalLine(context, i + k, j, l, z, background);
        renderBorder(context, i, j + 1, k, l, z, borderColorStart, borderColorEnd);
    }

    private static void renderBorder(DrawContext context, int x, int y, int width, int height, int z, int startColor, int endColor) {
        renderVerticalLine(context, x, y, height - 2, z, startColor, endColor);
        renderVerticalLine(context, x + width - 1, y, height - 2, z, startColor, endColor);
        renderHorizontalLine(context, x, y - 1, width, z, startColor);
        renderHorizontalLine(context, x, y - 1 + height - 1, width, z, endColor);
    }

    private static void renderVerticalLine(DrawContext context, int x, int y, int height, int z, int color) {
        context.fill(x, y, x + 1, y + height, z, color);
    }

    private static void renderVerticalLine(DrawContext context, int x, int y, int height, int z, int startColor, int endColor) {
        context.fillGradient(x, y, x + 1, y + height, z, startColor, endColor);
    }

    private static void renderHorizontalLine(DrawContext context, int x, int y, int width, int z, int color) {
        context.fill(x, y, x + width, y + 1, z, color);
    }

    private static void renderRectangle(DrawContext context, int x, int y, int width, int height, int z, int color) {
        context.fill(x, y, x + width, y + height, z, color);
    }
}
