package net.levelz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.level.restriction.ComponentRestriction;
import net.levelz.level.restriction.PlayerRestriction;
import net.levelz.registry.EnchantmentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class TooltipUtil {

    public static void renderItemTooltip(Minecraft client, ItemStack stack, List<Component> lines) {
        if (client.player != null) {
            LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
            boolean isCreative = client.player.isCreative(); // Add all lines, not only the missing ones

            if (stack.getItem() instanceof BlockItem blockItem) {
                int blockId = BuiltInRegistries.BLOCK.getId(blockItem.getBlock());
                if (isCreative || !levelManager.hasRequiredBlockLevel(blockItem.getBlock())) {
                    if (LevelManager.BLOCK_RESTRICTIONS.containsKey(blockId)) {
                        PlayerRestriction playerRestriction = LevelManager.BLOCK_RESTRICTIONS.get(blockId);
                        lines.add(Component.translatable("restriction.levelz.usable.tooltip"));
                        for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                            if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                lines.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(ChatFormatting.RED));
                            }
                        }
                    }
                }
                if (isCreative || !levelManager.hasRequiredMiningLevel(blockItem.getBlock())) {
                    if (LevelManager.MINING_RESTRICTIONS.containsKey(blockId)) {
                        PlayerRestriction playerRestriction = LevelManager.MINING_RESTRICTIONS.get(blockId);
                        lines.add(Component.translatable("restriction.levelz.mineable.tooltip"));
                        for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                            if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                lines.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(ChatFormatting.RED));
                            }
                        }
                    }
                }
            }
            int itemId = BuiltInRegistries.ITEM.getId(stack.getItem());
            if (isCreative || !levelManager.hasRequiredItemLevel(stack.getItem())) {
                if (LevelManager.ITEM_RESTRICTIONS.containsKey(itemId)) {
                    PlayerRestriction playerRestriction = LevelManager.ITEM_RESTRICTIONS.get(itemId);
                    lines.add(Component.translatable("restriction.levelz.usable.tooltip"));
                    for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                        if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                            lines.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(ChatFormatting.RED));
                        }
                    }
                }
            }
            List<ComponentRestriction> componentRestrictions = levelManager.getComponentRestrictions(stack);
            if (!componentRestrictions.isEmpty() && (isCreative || !levelManager.hasRequiredComponentLevel(stack))) {
                lines.add(Component.translatable("restriction.levelz.usable.tooltip"));
                for (ComponentRestriction componentRestriction : componentRestrictions) {
                    for (Map.Entry<Integer, Integer> entry : componentRestriction.getSkillLevelRestrictions().entrySet()) {
                        if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                            lines.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(ChatFormatting.RED));
                        }
                    }
                }
            }
            if (ConfigInit.CONFIG.restrictEnchantedItems) {
                boolean addedHeader = false;
                for (Object2IntMap.Entry<Holder<Enchantment>> enchantmentEntry : stack.getEnchantments().entrySet()) {
                    if (isCreative || !levelManager.hasRequiredEnchantmentLevel(enchantmentEntry.getKey(), enchantmentEntry.getIntValue())) {
                        int enchantmentId = EnchantmentRegistry.getId(enchantmentEntry.getKey(), enchantmentEntry.getIntValue());
                        if (LevelManager.ENCHANTMENT_RESTRICTIONS.containsKey(enchantmentId)) {
                            if (!addedHeader) {
                                lines.add(Component.translatable("restriction.levelz.usable.tooltip"));
                                addedHeader = true;
                            }
                            lines.add(Enchantment.getFullname(enchantmentEntry.getKey(), enchantmentEntry.getIntValue()));
                            for (Map.Entry<Integer, Integer> entry : LevelManager.ENCHANTMENT_RESTRICTIONS.get(enchantmentId).getSkillLevelRestrictions().entrySet()) {
                                if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                    lines.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(ChatFormatting.RED));
                                }
                            }
                        }
                    }
                }
            }
            if (isCreative || !levelManager.hasRequiredCraftingLevel(stack.getItem())) {
                if (LevelManager.CRAFTING_RESTRICTIONS.containsKey(itemId)) {
                    PlayerRestriction playerRestriction = LevelManager.CRAFTING_RESTRICTIONS.get(itemId);
                    lines.add(Component.translatable("restriction.levelz.craftable.tooltip"));
                    for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                        if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                            lines.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(ChatFormatting.RED));
                        }
                    }
                }
            }
            if (stack.getItem() instanceof SpawnEggItem spawnEggItem) {
                if (isCreative || !levelManager.hasRequiredEntityLevel(spawnEggItem.getType(stack))) {
                    int entityId = BuiltInRegistries.ENTITY_TYPE.getId(spawnEggItem.getType(stack));
                    if (LevelManager.ENTITY_RESTRICTIONS.containsKey(entityId)) {
                        PlayerRestriction playerRestriction = LevelManager.ENTITY_RESTRICTIONS.get(entityId);
                        lines.add(Component.translatable("restriction.levelz.usable.tooltip"));
                        for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                            if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                lines.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(ChatFormatting.RED));
                            }
                        }
                    }
                }
            }
        }
    }

    // Recommend to play with https://www.curseforge.com/minecraft/mc-mods/health-overlay-fabric
    public static void renderTooltip(Minecraft client, GuiGraphics context) {
        if (client.hitResult != null && ConfigInit.CONFIG.showLockedBlockInfo) {

            HitResult hitResult = client.hitResult;
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
                EntityType<?> entityType = ((EntityHitResult) hitResult).getEntity().getType();
                if (!levelManager.hasRequiredEntityLevel(entityType)) {
                    List<Component> textList = new ArrayList<>();
                    textList.add(Component.nullToEmpty(entityType.getDescription().getString()));
                    for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredEntityLevel(entityType).entrySet()) {
                        ChatFormatting formatting =
                                levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? ChatFormatting.RED : ChatFormatting.GREEN;
                        textList.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(formatting));
                    }
                    renderTooltip(client, context, textList,
                            null, context.guiWidth() / 2 + ConfigInit.CONFIG.lockedBlockInfoPosX, ConfigInit.CONFIG.lockedBlockInfoPosY);
                }
            } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                Block block = client.level.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getBlock();
                LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
                List<Component> textList = new ArrayList<>();
                if (!levelManager.hasRequiredMiningLevel(block)) {
                    textList.add(Component.nullToEmpty(block.getName().getString()));
                    // textList.add(Text.translatable("item.levelz.mineable.tooltip"));
                    for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredMiningLevel(block).entrySet()) {
                        ChatFormatting formatting =
                                levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? ChatFormatting.RED : ChatFormatting.GREEN;
                        textList.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(formatting));
                    }
                }
                if (!levelManager.hasRequiredBlockLevel(block)) {
                    if (textList.isEmpty()) {
                        textList.add(Component.nullToEmpty(block.getName().getString()));
                    }
                    textList.add(Component.translatable("restriction.levelz.block_usage"));
                    for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredBlockLevel(block).entrySet()) {
                        ChatFormatting formatting =
                                levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? ChatFormatting.RED : ChatFormatting.GREEN;
                        textList.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(formatting));
                    }
                }
                if (!textList.isEmpty()) {
                    renderTooltip(client, context, textList,
                            BuiltInRegistries.BLOCK.getKey(block), context.guiWidth() / 2 + ConfigInit.CONFIG.lockedBlockInfoPosX, ConfigInit.CONFIG.lockedBlockInfoPosY);
                }
            }
        }
    }

    private static void renderTooltip(Minecraft client, GuiGraphics context, List<Component> textList, @Nullable ResourceLocation identifier, int x, int y) {
        int maxTextWidth = 0;
        for (int i = 0; i < textList.size(); i++) {
            if (client.font.width(textList.get(i)) > maxTextWidth) {
                maxTextWidth = client.font.width(textList.get(i));
                if (i == 0 && identifier != null) {
                    maxTextWidth += 22;
                }
            }
        }
        maxTextWidth += 5;

        context.pose().pushPose();

        int colorStart = 0xBF191919; // background
        int colorTwo = 0xBF7F0200; // light border
        int colorThree = 0xBF380000; // darker border

        render(context, x - maxTextWidth / 2 - 3, y + 4, maxTextWidth, textList.size() * 10 + 11, 400, colorStart, colorTwo, colorThree);

        context.pose().translate(0.0, 0.0, 400.0);

        int i = 9;
        for (Component text : textList) {
            if (i == 9) {
                context.drawString(client.font, text, x - maxTextWidth / 2 + (identifier != null ? 20 : 0), y + i, 0xFFFFFF, false);
            } else {
                context.drawString(client.font, text, x - maxTextWidth / 2, y + i + 8, 0xFFFFFF, false);
            }
            i += 10;
        }

        if (identifier != null) {
            context.renderItem(BuiltInRegistries.ITEM.get(identifier).getDefaultInstance(), x - maxTextWidth / 2, y + 5);
        }
        context.pose().popPose();
    }

    public static void render(GuiGraphics context, int x, int y, int width, int height, int z, int background, int borderColorStart, int borderColorEnd) {
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

    private static void renderBorder(GuiGraphics context, int x, int y, int width, int height, int z, int startColor, int endColor) {
        renderVerticalLine(context, x, y, height - 2, z, startColor, endColor);
        renderVerticalLine(context, x + width - 1, y, height - 2, z, startColor, endColor);
        renderHorizontalLine(context, x, y - 1, width, z, startColor);
        renderHorizontalLine(context, x, y - 1 + height - 1, width, z, endColor);
    }

    private static void renderVerticalLine(GuiGraphics context, int x, int y, int height, int z, int color) {
        context.fill(x, y, x + 1, y + height, z, color);
    }

    private static void renderVerticalLine(GuiGraphics context, int x, int y, int height, int z, int startColor, int endColor) {
        context.fillGradient(x, y, x + 1, y + height, z, startColor, endColor);
    }

    private static void renderHorizontalLine(GuiGraphics context, int x, int y, int width, int z, int color) {
        context.fill(x, y, x + width, y + 1, z, color);
    }

    private static void renderRectangle(GuiGraphics context, int x, int y, int width, int height, int z, int color) {
        context.fill(x, y, x + width, y + height, z, color);
    }

    public static void renderHorizontalLine(GuiGraphics context, int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
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
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        VertexConsumer vertexConsumer = context.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix4f = context.pose().last().pose();

        vertexConsumer.addVertex(matrix4f, right, top, zLevel).setColor(endRed, endGreen, endBlue, endAlpha);
        vertexConsumer.addVertex(matrix4f, left, top, zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        vertexConsumer.addVertex(matrix4f, left, bottom, zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        vertexConsumer.addVertex(matrix4f, right, bottom, zLevel).setColor(endRed, endGreen, endBlue, endAlpha);
        context.flush();
        RenderSystem.disableBlend();
    }

}
