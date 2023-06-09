package net.levelz.screen.widget;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.screen.SkillInfoScreen;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SkillScrollableWidget extends ScrollableWidget {

    private final List<Text> textList;
    private final String title;
    private final TextRenderer textRenderer;

    private final ArrayList<Object> sortedUnlockSkillList = new ArrayList<Object>();

    private int totalYSpace = 0;
    private int ySpace = 0;

    private boolean scrollbarDragged;

    public SkillScrollableWidget(int x, int y, int width, int height, List<Text> textList, String title, TextRenderer textRenderer) {
        super(x, y, width, height, Text.of(""));
        this.title = title;
        this.textRenderer = textRenderer;
        this.textList = textList;
        // Special Item
        // 0: material, 1: skill, 2: level, 3: info, 4: boolean
        // Other
        // 0: skill, 1: level, 2: info, 3: boolean
        ArrayList<Object> unlockSkillList = new ArrayList<Object>();

        // Fill skill list
        for (int o = 0; o < LevelLists.listOfAllLists.size(); o++) {
            if (!LevelLists.listOfAllLists.get(o).isEmpty()) {
                if (LevelLists.listOfAllLists.get(o).get(0).toString().equals(this.title)) {
                    unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(1));
                    unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(2));
                    unlockSkillList.add(null);
                } else
                    for (int k = 0; k < LevelLists.listOfAllLists.get(o).size(); k += 5)
                        if (LevelLists.listOfAllLists.get(o).get(k + 1).toString().equals(this.title)) {
                            unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(2 + k));
                            unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(3 + k));
                            unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(0 + k));
                        }
            }
        }
        // Sort list
        // If I set j = 0 it will include 0 level unlocks!
        for (int k = 1; k <= ConfigInit.CONFIG.maxLevel; k++) {
            for (int o = 0; o < unlockSkillList.size(); o += 3) {
                if (unlockSkillList.get(o).equals(k)) {
                    if (!sortedUnlockSkillList.contains(unlockSkillList.get(o))) {
                        sortedUnlockSkillList.add(unlockSkillList.get(o));
                    }
                    sortedUnlockSkillList.add(unlockSkillList.get(o + 1));
                    sortedUnlockSkillList.add(unlockSkillList.get(o + 2));
                }
            }
        }
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

        if (textList.size() > 0 && translatableTextIsNotBlank(textList.get(0))) {
            context.drawText(this.textRenderer, textList.get(0), this.getX(), this.ySpace, 0x3F3F3F, false);
            this.ySpace += 14;
        }
        if (textList.size() > 1 && translatableTextIsNotBlank(textList.get(1))) {
            context.drawText(this.textRenderer, textList.get(1), this.getX(), this.ySpace, 0x3F3F3F, false);
            this.ySpace += 14;
        }
        if (textList.size() > 2 && translatableTextIsNotBlank(textList.get(2))) {
            context.drawText(this.textRenderer, textList.get(2), this.getX(), this.ySpace, 0x3F3F3F, false);
            if (textList.size() > 3 && translatableTextIsNotBlank(textList.get(3))) {
                this.ySpace += 10;
                context.drawText(this.textRenderer, textList.get(3), this.getX(), this.ySpace, 0x3F3F3F, false);
            }
            this.ySpace += 14;
        }
        if (textList.size() > 4 && translatableTextIsNotBlank(textList.get(4))) {
            context.drawText(this.textRenderer, textList.get(4), this.getX(), this.ySpace, 0x3F3F3F, false);
            if (textList.size() > 5 && translatableTextIsNotBlank(textList.get(5))) {
                this.ySpace += 10;
                context.drawText(this.textRenderer, textList.get(5), this.getX(), this.ySpace, 0x3F3F3F, false);
            }
            this.ySpace += 14;
        }

        boolean hasLvlMaxUnlock = false;
        this.ySpace += 10;
        context.drawText(this.textRenderer, Text.translatable("text.levelz.general_info"), this.getX(), this.ySpace, 0x3F3F3F, false);
        this.ySpace += 16;
        // level, object, info, object, info,..., level,...
        for (int u = 0; u < sortedUnlockSkillList.size(); u++) {
            if (sortedUnlockSkillList.get(u) != null && sortedUnlockSkillList.get(u).getClass() == Integer.class) {
                // Add level category info
                context.drawText(this.textRenderer, Text.translatable("text.levelz.level", sortedUnlockSkillList.get(u)), this.getX(), this.ySpace, 0x3F3F3F, false);
                this.ySpace += 16;
                for (int g = 1; g < sortedUnlockSkillList.size() - u; g += 2) {
                    if (sortedUnlockSkillList.get(u + g).getClass() == Integer.class) {
                        break;
                    }
                    String string = sortedUnlockSkillList.get(u + g).toString();
                    if (string.contains("minecraft:custom_")) {
                        string = sortedUnlockSkillList.get(u + g + 1).toString();
                    }
                    Identifier identifier = new Identifier(string);
                    boolean hit = true;

                    if (!Registries.BLOCK.get(identifier).equals(Blocks.AIR)) {
                        string = Registries.BLOCK.get(identifier).getTranslationKey();
                    } else if (!Registries.ITEM.get(identifier).equals(Items.AIR)) {
                        string = Registries.ITEM.get(identifier).getTranslationKey();
                    } else if (!Registries.ENTITY_TYPE.get(identifier).equals(EntityType.PIG) && !EntityType.getId(EntityType.PIG).equals(identifier)) {
                        string = Registries.ENTITY_TYPE.get(identifier).getTranslationKey();
                    } else {
                        hit = false;
                    }
                    Language language = Language.getInstance();

                    if (hit)
                        string = language.get(string);
                    else {
                        String translationKey = String.format("text.levelz.object_info.%s", identifier.getPath());
                        if (language.hasTranslation(translationKey)) {
                            string = language.get(translationKey);
                        } else {
                            string = StringUtils.capitalize(string.replace("minecraft:", "").replaceAll("_", " ").replace(':', ' '));
                        }
                    }

                    if (sortedUnlockSkillList.get(u + g + 1) != null && !sortedUnlockSkillList.get(u + g).toString().contains("minecraft:custom_")) {
                        String otherString = sortedUnlockSkillList.get(u + g + 1).toString();
                        String translationKey = String.format("text.levelz.object_prefix.%s", otherString);
                        if (language.hasTranslation(translationKey)) {
                            otherString = language.get(translationKey);
                        } else {
                            otherString = otherString.replace('_', ' ');
                        }
                        context.drawText(this.textRenderer, Text.translatable("text.levelz.object_info_2", StringUtils.capitalize(otherString), string), this.getX() + 10, this.ySpace, 0x3F3F3F,
                                false);
                    } else {
                        context.drawText(this.textRenderer, Text.translatable("text.levelz.object_info_1", string), this.getX() + 10, this.ySpace, 0x3F3F3F, false);
                    }
                    this.ySpace += 16;
                }
                if (sortedUnlockSkillList.get(u).equals(ConfigInit.CONFIG.maxLevel)) {
                    hasLvlMaxUnlock = true;
                } else {
                    this.ySpace += 4;
                }
            }

        }

        if (!hasLvlMaxUnlock) {
            context.drawText(this.textRenderer, Text.translatable("text.levelz.level", ConfigInit.CONFIG.maxLevel), this.getX(), this.ySpace, 0x3F3F3F, false);
        } else {
            this.ySpace -= 16;
        }
        if (textList.size() > 6 && translatableTextIsNotBlank(textList.get(6))) {
            this.ySpace += 16;
            context.drawText(this.textRenderer, textList.get(6), this.getX() + 10, this.ySpace, 0x3F3F3F, false);
            if (textList.size() > 7 && translatableTextIsNotBlank(textList.get(7))) {
                this.ySpace += 10;
                context.drawText(this.textRenderer, textList.get(7), this.getX() + 10, this.ySpace, 0x3F3F3F, false);
            }
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
        return mouseX >= (double) this.getX() && mouseX < (double) (this.getX() + this.width + 1) && mouseY >= (double) this.getY() && mouseY < (double) (this.getY() + this.height);
    }

    private int getContentsHeightWithPadding() {
        return this.getContentsHeight() + 4;
    }

    private int getScrollbarThumbHeight() {
        return MathHelper.clamp((int) ((float) (this.height * this.height) / (float) this.getContentsHeightWithPadding()), 32, this.height);
    }

    private boolean translatableTextIsNotBlank(@Nullable Text text) {
        if (text == null) {
            return false;
        }
        if (!Texts.hasTranslation(text)) {
            return false;
        }
        return !Language.getInstance().get(text.getString()).isBlank();
    }

}
