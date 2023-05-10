package net.levelz.screen.widget;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.mixin.misc.ItemRendererAccessor;
import net.levelz.screen.SkillInfoScreen;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class SkillListScrollableWidget extends ScrollableWidget {

    private final String title;
    private final boolean mining;

    private final List<Integer> levelList;
    private final List<List<Integer>> objectList;
    private final List<String> skillList;

    private final Screen screen;
    private final TextRenderer textRenderer;
    private final ItemRenderer itemRenderer;

    private int totalYSpace = 0;
    private int ySpace = 0;

    private boolean scrollbarDragged;

    public SkillListScrollableWidget(int x, int y, int width, int height, List<Integer> levelList, List<List<Integer>> objectList, List<String> skillList, String title, Screen screen,
            TextRenderer textRenderer, ItemRenderer itemRenderer) {
        super(x, y, width, height, Text.of(""));
        this.title = title;
        this.screen = screen;
        this.textRenderer = textRenderer;
        this.itemRenderer = itemRenderer;
        this.levelList = levelList;
        this.objectList = objectList;
        this.skillList = skillList;
        this.mining = this.title.equals("mining");
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder narrationMessageBuilder) {
    }

    @Override
    protected int getContentsHeight() {
        return this.totalYSpace;
    }

    @Override
    protected boolean overflows() {
        return this.totalYSpace > 185;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 27;
    }

    @Override
    protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.ySpace = this.y;
        // 9 objects next to each other
        for (int u = 0; u < levelList.size(); u++) {
            if (objectList.get(u).isEmpty()) {
                continue;
            }

            if (!skillList.isEmpty()) {
                this.textRenderer.draw(matrices, Text.translatable("item.levelz." + skillList.get(u) + ".tooltip", levelList.get(u)), this.x, this.ySpace, 0x3F3F3F);
            } else {
                this.textRenderer.draw(matrices, Text.translatable("text.levelz.level", levelList.get(u)), this.x, this.ySpace, 0x3F3F3F);
            }

            int listSplitter = 0;
            int gridXSpace = this.x;
            this.ySpace += 16;

            for (int k = 0; k < objectList.get(u).size(); k++) {
                ItemStack stack = null;
                List<Text> tooltip = new ArrayList<Text>();
                if (this.mining) {
                    Block block = Registry.BLOCK.get(objectList.get(u).get(k));
                    stack = new ItemStack(block);
                    tooltip.add(block.getName());
                } else {
                    Item item = Registry.ITEM.get(objectList.get(u).get(k));
                    tooltip.add(item.getName());
                    stack = item.getDefaultStack();

                    if (BrewingRecipeRegistry.isValidIngredient(item.getDefaultStack()) && LevelLists.potionList.contains(item)) {
                        int index = LevelLists.potionList.indexOf(item);
                        Potion potion = (Potion) LevelLists.potionList.get(index + 1);
                        ItemStack potionStack = PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
                        tooltip.add(Text.of("Ingredient for " + Text.translatable(((PotionItem) PotionUtil.setPotion(potionStack, potion).getItem()).getTranslationKey(potionStack)).getString()));
                    }
                }
                if (stack != null) {
                    this.renderGuiItemModel(matrices, stack, gridXSpace, this.ySpace);
                    if (!tooltip.isEmpty() && this.isPointWithinBounds(gridXSpace - this.x, this.ySpace - this.y - (int) this.getScrollY(), 16, 16, mouseX, mouseY)) {
                        ScrollableWidget.disableScissor();
                        matrices.push();
                        matrices.translate(0.0, this.getScrollY(), 0.0);
                        this.screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
                        matrices.pop();
                        ScrollableWidget.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
                    }
                }
                gridXSpace += 18;
                listSplitter++;
                if (listSplitter % 9 == 0 || k == objectList.get(u).size() - 1) {
                    this.ySpace += 18;
                    gridXSpace = this.x;
                }
            }
            this.ySpace += 8;
        }
        if (this.totalYSpace == 0) {
            this.totalYSpace = this.ySpace - this.y;
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ScrollableWidget.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        matrices.push();
        matrices.translate(0.0, -getScrollY(), 0.0);
        this.renderContents(matrices, mouseX, mouseY, delta);
        matrices.pop();
        ScrollableWidget.disableScissor();
        this.renderOverlay(matrices);
    }

    @Override
    protected void renderOverlay(MatrixStack matrices) {
        if (this.overflows()) {
            int l = Math.max(this.y + 1, (int) this.getScrollY() * (this.height - 27) / this.getMaxScrollY() + this.y - 1);
            RenderSystem.setShaderTexture(0, SkillInfoScreen.BACKGROUND_TEXTURE);
            this.drawTexture(matrices, this.x + 177, this.y, 200, 0, 8, 185);
            this.drawTexture(matrices, this.x + 178, l, 208, 0, 6, 27);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.visible) {
            return false;
        }
        this.setScrollY(this.getScrollY() - amount * this.getDeltaYPerScroll());
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.scrollbarDragged = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible) {
            return false;
        }
        boolean bl = this.isWithinBounds(mouseX, mouseY);
        boolean bl2 = this.overflows() && mouseX >= (double) (this.x + this.width - 5) && mouseX <= (double) (this.x + this.width + 1) && mouseY >= (double) this.y
                && mouseY < (double) (this.y + this.height);
        this.setFocused(bl || bl2);
        if (bl2 && button == 0) {
            this.scrollbarDragged = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!(this.visible && this.isFocused() && this.scrollbarDragged)) {
            return false;
        }
        if (mouseY < (double) this.y) {
            this.setScrollY(0.0);
        } else if (mouseY > (double) (this.y + this.height)) {
            this.setScrollY(this.getMaxScrollY());
        } else {
            int i = this.getScrollbarThumbHeight();
            double d = Math.max(1, this.getMaxScrollY() / (this.height - i));
            this.setScrollY(this.getScrollY() + deltaY * d);
        }
        return true;
    }

    @Override
    protected boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= (double) this.x && mouseX < (double) (this.x + this.width + 1) && mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
    }

    private int getContentsHeightWithPadding() {
        return this.getContentsHeight() + 4;
    }

    private int getScrollbarThumbHeight() {
        return MathHelper.clamp((int) ((float) (this.height * this.height) / (float) this.getContentsHeightWithPadding()), 32, this.height);
    }

    private boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        int i = this.x;
        int j = this.y;
        return (pointX -= (double) i) >= (double) (x - 1) && pointX < (double) (x + width + 1) && (pointY -= (double) j) >= (double) (y - 1) && pointY < (double) (y + height + 1);
    }

    private void renderGuiItemModel(MatrixStack matrices, ItemStack stack, int x, int y) {
        BakedModel model = this.itemRenderer.getModel(stack, null, null, 0);

        ((ItemRendererAccessor) this.itemRenderer).getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0f);
        matrixStack.translate(8.0, 8.0, 0.0);
        matrixStack.scale(1.0f, -1.0f, 1.0f);
        matrixStack.scale(16.0f, 16.0f, 16.0f);

        RenderSystem.applyModelViewMatrix();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        matrices.push();
        matrices.translate(0.0, this.getScrollY() * 1.0625D, 0.0);
        this.itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrices, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
        matrices.pop();
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

}
