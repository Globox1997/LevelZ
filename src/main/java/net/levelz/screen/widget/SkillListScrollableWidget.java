package net.levelz.screen.widget;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.screen.SkillInfoScreen;
import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SkillListScrollableWidget extends ScrollableWidget {

    private final String title;
    private final boolean mining;

    private final List<Integer> levelList;
    private final List<List<Integer>> objectList;
    private final List<String> skillList;

    private final TextRenderer textRenderer;

    private int totalYSpace = 0;
    private int ySpace = 0;

    private boolean scrollbarDragged;

    public SkillListScrollableWidget(int x, int y, int width, int height, List<Integer> levelList, List<List<Integer>> objectList, List<String> skillList, String title, TextRenderer textRenderer) {
        super(x, y, width, height, Text.of(""));
        this.title = title;
        this.textRenderer = textRenderer;
        this.levelList = levelList;
        this.objectList = objectList;
        this.skillList = skillList;
        this.mining = this.title.equals("mining");
    }

    @Override
    protected void appendDefaultNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder var1) {
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
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        this.ySpace = this.getY();
        // 9 objects next to each other
        for (int u = 0; u < levelList.size(); u++) {
            if (objectList.get(u).isEmpty()) {
                continue;
            }

            if (!skillList.isEmpty()) {
                context.drawText(this.textRenderer, Text.translatable("item.levelz." + skillList.get(u) + ".tooltip", levelList.get(u)), this.getX(), this.ySpace, 0x3F3F3F, false);
            } else {
                context.drawText(this.textRenderer, Text.translatable("text.levelz.level", levelList.get(u)), this.getX(), this.ySpace, 0x3F3F3F, false);
            }

            int listSplitter = 0;
            int gridXSpace = this.getX();
            this.ySpace += 16;

            for (int k = 0; k < objectList.get(u).size(); k++) {
                ItemStack stack = null;
                List<Text> tooltip = new ArrayList<Text>();
                if (this.mining) {
                    Block block = Registries.BLOCK.get(objectList.get(u).get(k));
                    stack = new ItemStack(block);
                    tooltip.add(block.getName());
                } else {
                    Item item = Registries.ITEM.get(objectList.get(u).get(k));
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
                    context.drawItem(stack, gridXSpace, this.ySpace);

                    if (!tooltip.isEmpty() && this.isPointWithinBounds(gridXSpace - this.getX(), this.ySpace - this.getY() - (int) this.getScrollY(), 16, 16, mouseX, mouseY)) {
                        context.disableScissor();
                        context.getMatrices().push();
                        context.getMatrices().translate(0.0, this.getScrollY(), 0.0);
                        context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
                        context.drawText(this.textRenderer, "", 0, 0, 0, false); // need this cause of render bug
                        context.getMatrices().pop();
                        context.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
                    }
                }
                gridXSpace += 18;
                listSplitter++;
                if (listSplitter % 9 == 0 || k == objectList.get(u).size() - 1) {
                    this.ySpace += 18;
                    gridXSpace = this.getX();
                }
            }
            this.ySpace += 8;
        }
        if (this.totalYSpace == 0) {
            this.totalYSpace = this.ySpace - this.getY();
        }
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
        context.getMatrices().push();
        context.getMatrices().translate(0.0, -getScrollY(), 0.0);
        this.renderContents(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
        context.disableScissor();
        this.renderOverlay(context);
    }

    @Override
    protected void renderOverlay(DrawContext context) {
        if (this.overflows()) {
            int l = Math.max(this.getY() + 1, (int) this.getScrollY() * (this.height - 27) / this.getMaxScrollY() + this.getY() - 1);
            context.drawTexture(SkillInfoScreen.BACKGROUND_TEXTURE, this.getX() + 177, this.getY(), 200, 0, 8, 185);
            context.drawTexture(SkillInfoScreen.BACKGROUND_TEXTURE, this.getX() + 178, l, 208, 0, 6, 27);
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
        boolean bl2 = this.overflows() && mouseX >= (double) (this.getX() + this.width - 5) && mouseX <= (double) (this.getX() + this.width + 1) && mouseY >= (double) this.getY()
                && mouseY < (double) (this.getY() + this.height);
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
        if (mouseY < (double) this.getY()) {
            this.setScrollY(0.0);
        } else if (mouseY > (double) (this.getY() + this.height)) {
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
        return mouseX >= (double) this.getX() && mouseX < (double) (this.getY() + this.width + 1) && mouseY >= (double) this.getY() && mouseY < (double) (this.getY() + this.height);
    }

    private int getContentsHeightWithPadding() {
        return this.getContentsHeight() + 4;
    }

    private int getScrollbarThumbHeight() {
        return MathHelper.clamp((int) ((float) (this.height * this.height) / (float) this.getContentsHeightWithPadding()), 32, this.height);
    }

    private boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        int i = this.getX();
        int j = this.getY();
        return (pointX -= (double) i) >= (double) (x - 1) && pointX < (double) (x + width + 1) && (pointY -= (double) j) >= (double) (y - 1) && pointY < (double) (y + height + 1);
    }

}
