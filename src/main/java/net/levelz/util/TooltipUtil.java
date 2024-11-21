package net.levelz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.level.restriction.PlayerRestriction;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class TooltipUtil {

    public static void renderItemTooltip(MinecraftClient client, ItemStack stack, List<Text> lines) {
        if (client.player != null) {
            LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
            boolean isCreative = client.player.isCreative(); // Add all lines, not only the missing ones

            if (stack.getItem() instanceof BlockItem blockItem) {
                int blockId = Registries.BLOCK.getRawId(blockItem.getBlock());
                if (isCreative || !levelManager.hasRequiredBlockLevel(blockItem.getBlock())) {
                    if (LevelManager.BLOCK_RESTRICTIONS.containsKey(blockId)) {
                        PlayerRestriction playerRestriction = LevelManager.BLOCK_RESTRICTIONS.get(blockId);
                        lines.add(Text.translatable("restriction.levelz.usable.tooltip"));
                        for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                            if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                lines.add(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(Formatting.RED));
                            }
                        }
                    }
                }
                if (isCreative || !levelManager.hasRequiredMiningLevel(blockItem.getBlock())) {
                    if (LevelManager.MINING_RESTRICTIONS.containsKey(blockId)) {
                        PlayerRestriction playerRestriction = LevelManager.MINING_RESTRICTIONS.get(blockId);
                        lines.add(Text.translatable("restriction.levelz.mineable.tooltip"));
                        for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                            if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                lines.add(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(Formatting.RED));
                            }
                        }
                    }
                }
            }
            int itemId = Registries.ITEM.getRawId(stack.getItem());
            if (isCreative || !levelManager.hasRequiredItemLevel(stack.getItem())) {
                if (LevelManager.ITEM_RESTRICTIONS.containsKey(itemId)) {
                    PlayerRestriction playerRestriction = LevelManager.ITEM_RESTRICTIONS.get(itemId);
                    lines.add(Text.translatable("restriction.levelz.usable.tooltip"));
                    for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                        if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                            lines.add(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(Formatting.RED));
                        }
                    }
                }
            }
            if (isCreative || !levelManager.hasRequiredCraftingLevel(stack.getItem())) {
                if (LevelManager.CRAFTING_RESTRICTIONS.containsKey(itemId)) {
                    PlayerRestriction playerRestriction = LevelManager.CRAFTING_RESTRICTIONS.get(itemId);
                    lines.add(Text.translatable("restriction.levelz.craftable.tooltip"));
                    for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                        if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                            lines.add(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(Formatting.RED));
                        }
                    }
                }
            }
            if (stack.getItem() instanceof SpawnEggItem spawnEggItem) {
                if (isCreative || !levelManager.hasRequiredEntityLevel(spawnEggItem.getEntityType(stack))) {
                    int entityId = Registries.ENTITY_TYPE.getRawId(spawnEggItem.getEntityType(stack));
                    if (LevelManager.ENTITY_RESTRICTIONS.containsKey(entityId)) {
                        PlayerRestriction playerRestriction = LevelManager.ENTITY_RESTRICTIONS.get(entityId);
                        lines.add(Text.translatable("restriction.levelz.usable.tooltip"));
                        for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                            if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                lines.add(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(Formatting.RED));
                            }
                        }
                    }
                }
            }
        }
    }

    // Recommend to play with https://www.curseforge.com/minecraft/mc-mods/health-overlay-fabric
    public static void renderTooltip(MinecraftClient client, DrawContext context) {
        if (client.crosshairTarget != null && ConfigInit.CONFIG.showLockedBlockInfo) {

            HitResult hitResult = client.crosshairTarget;
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
                EntityType<?> entityType = ((EntityHitResult) hitResult).getEntity().getType();
                if (!levelManager.hasRequiredEntityLevel(entityType)) {
                    List<Text> textList = new ArrayList<>();
                    textList.add(Text.of(entityType.getName().getString()));
                    for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredEntityLevel(entityType).entrySet()) {
                        Formatting formatting =
                                levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? Formatting.RED : Formatting.GREEN;
                        textList.add(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(formatting));
                    }
                    renderTooltip(client, context, textList,
                            null, context.getScaledWindowWidth() / 2 + ConfigInit.CONFIG.lockedBlockInfoPosX, ConfigInit.CONFIG.lockedBlockInfoPosY);
                }
            } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                Block block = client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getBlock();
                LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
                List<Text> textList = new ArrayList<>();
                if (!levelManager.hasRequiredMiningLevel(block)) {
                    textList.add(Text.of(block.getName().getString()));
                    // textList.add(Text.translatable("item.levelz.mineable.tooltip"));
                    for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredMiningLevel(block).entrySet()) {
                        Formatting formatting =
                                levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? Formatting.RED : Formatting.GREEN;
                        textList.add(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(formatting));
                    }
                }
                if (!levelManager.hasRequiredBlockLevel(block)) {
                    if (textList.isEmpty()) {
                        textList.add(Text.of(block.getName().getString()));
                    }
                    textList.add(Text.translatable("restriction.levelz.block_usage"));
                    for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredBlockLevel(block).entrySet()) {
                        Formatting formatting =
                                levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? Formatting.RED : Formatting.GREEN;
                        textList.add(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(formatting));
                    }
                }
                if (!textList.isEmpty()) {
                    renderTooltip(client, context, textList,
                            Registries.BLOCK.getId(block), context.getScaledWindowWidth() / 2 + ConfigInit.CONFIG.lockedBlockInfoPosX, ConfigInit.CONFIG.lockedBlockInfoPosY);
                }
            }
        }
    }

    private static void renderTooltip(MinecraftClient client, DrawContext context, List<Text> textList, @Nullable Identifier identifier, int x, int y) {
        int maxTextWidth = 0;
        for (int i = 0; i < textList.size(); i++) {
            if (client.textRenderer.getWidth(textList.get(i)) > maxTextWidth) {
                maxTextWidth = client.textRenderer.getWidth(textList.get(i));
                if (i == 0 && identifier != null) {
                    maxTextWidth += 22;
                }
            }
        }
        maxTextWidth += 5;

        context.getMatrices().push();

        int colorStart = 0xBF191919; // background
        int colorTwo = 0xBF7F0200; // light border
        int colorThree = 0xBF380000; // darker border

        render(context, x - maxTextWidth / 2 - 3, y + 4, maxTextWidth, textList.size() * 10 + 11, 400, colorStart, colorTwo, colorThree);

        context.getMatrices().translate(0.0, 0.0, 400.0);

        int i = 9;
        for (Text text : textList) {
            if (i == 9) {
                context.drawText(client.textRenderer, text, x - maxTextWidth / 2 + (identifier != null ? 20 : 0), y + i, 0xFFFFFF, false);
            } else {
                context.drawText(client.textRenderer, text, x - maxTextWidth / 2, y + i + 8, 0xFFFFFF, false);
            }
            i += 10;
        }

        if (identifier != null) {
            context.drawItem(Registries.ITEM.get(identifier).getDefaultStack(), x - maxTextWidth / 2, y + 5);
        }
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

        width -= 6;
        renderHorizontalLine(context, z, x + 3, y + 19, x + 3 + width / 2, y + 20, 0x007F0200, 0xBF7F0200);
        renderHorizontalLine(context, z, x + 3 + width / 2, y + 19, x + 3 + width, y + 20, 0xBF7F0200, 0x007F0200);
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

    public static void renderHorizontalLine(DrawContext context, int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, right, top, zLevel).color(endRed, endGreen, endBlue, endAlpha);
        vertexConsumer.vertex(matrix4f, left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha);
        vertexConsumer.vertex(matrix4f, left, bottom, zLevel).color(startRed, startGreen, startBlue, startAlpha);
        vertexConsumer.vertex(matrix4f, right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha);
        context.draw();
        RenderSystem.disableBlend();
    }

}
