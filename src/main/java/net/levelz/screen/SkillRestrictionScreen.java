package net.levelz.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.LevelzMain;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.level.LevelManager;
import net.levelz.level.restriction.PlayerRestriction;
import net.levelz.screen.widget.LineWidget;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class SkillRestrictionScreen extends Screen implements Tab {

    public static final Identifier BACKGROUND_TEXTURE = LevelzMain.identifierOf("textures/gui/skill_info_background.png");

    private final int backgroundWidth = 200;
    private final int backgroundHeight = 215;
    private int x;
    private int y;

    private final List<LineWidget> lines = new ArrayList<>();

    private final LevelManager levelManager;
    private Map<Integer, PlayerRestriction> restrictions;
    private final Component title;
    private final int code;

    private int lineIndex = 0;
    private boolean sortAlphabetical = false;

    private boolean draggingScrollbar = false;

    public SkillRestrictionScreen(LevelManager levelManager, Map<Integer, PlayerRestriction> restrictions, Component title, int code) {
        super(title);
        this.levelManager = levelManager;
        this.restrictions = restrictions;
        this.title = title;
        this.code = code;
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        sortRestrictions();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);

        context.text(this.font, this.title, this.x + 7, this.y + 7, 0xFF3F3F3F, false);

        for (int i = 0; i < 10; i++) {
            if (this.lines.size() <= i) {
                break;
            }
            int index = this.lineIndex + i;

            this.lines.get(index).render(context, this.x + 12, this.y + 24 + i * 18, mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        extractTransparentBackground(context);
        context.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);

        if (this.lines.size() > 10) {
            int scrollLevels = this.lines.size() - 10;
            int sliderY = this.lineIndex * 156 / scrollLevels;
            context.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.x + 186, this.y + 20 + sliderY, 200, 0, 6, 31, 256, 256);
        } else {
            context.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.x + 186, this.y + 20, 206, 0, 6, 31, 256, 256);
        }
        int sortU = LevelScreen.isPointWithinBounds(this.x + 179, this.y + 4, 14, 14, mouseX, mouseY) ? 14 : 0;
        int sortV = this.sortAlphabetical ? 180 : 166;
        context.blit(RenderPipelines.GUI_TEXTURED, LevelScreen.ICON_TEXTURE, this.x + 179, this.y + 4, sortU, sortV, 14, 14, 256, 256);

        DrawTabHelper.drawTab(minecraft, context, this, this.x, this.y, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.minecraft.options.keyInventory.matches(event)) {
            if (ConfigInit.CONFIG.switchScreen) {
                this.minecraft.gui.setScreen(new LevelScreen());
            } else {
                this.onClose();
            }
            return true;

        } else if (KeyInit.screenKey.matches(event)) {
            this.minecraft.gui.setScreen(new LevelScreen());
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        DrawTabHelper.onTabButtonClick(minecraft, this, this.x, this.y, mouseX, mouseY, false);
        if (this.lines.size() > 10 && LevelScreen.isPointWithinBounds(this.x + 186, this.y + 20, 6, 187, mouseX, mouseY)) {
            this.draggingScrollbar = true;
            updateScrollFromMouse(mouseY);
            return true;
        }
        if (LevelScreen.isPointWithinBounds(this.x + 179, this.y + 4, 14, 14, mouseX, mouseY)) {
            this.sortAlphabetical = !this.sortAlphabetical;
            sortRestrictions();
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.lines.size() > 10 && LevelScreen.isPointWithinBounds(this.x + 7, this.y + 19, 186, 189, mouseX, mouseY)) {
            int maxRow = this.lines.size() - 10;
            int newRow = this.lineIndex;
            newRow = newRow - (int) (verticalAmount);
            if (newRow < 0) {
                this.lineIndex = 0;
            } else {
                this.lineIndex = Math.min(newRow, maxRow);
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        double mouseY = event.y();
        if (this.draggingScrollbar) {
            updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.draggingScrollbar = false;
        return super.mouseReleased(event);
    }

    private void sortRestrictions() {
        if (this.code == 0) {
            if (this.sortAlphabetical) {
                this.restrictions = this.restrictions.entrySet().stream()
                        .sorted((entry1, entry2) -> {
                            String itemName1 = Component.translatable(BuiltInRegistries.ITEM.byId(entry1.getKey()).getDescriptionId()).getString();
                            String itemName2 = Component.translatable(BuiltInRegistries.ITEM.byId(entry2.getKey()).getDescriptionId()).getString();
                            return itemName1.compareToIgnoreCase(itemName2);
                        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            } else {
                this.restrictions = this.restrictions.entrySet().stream()
                        .sorted((entry1, entry2) -> {
                            int itemVar1 = entry1.getValue().getSkillLevelRestrictions().values().stream().findFirst().get();
                            int itemVar2 = entry2.getValue().getSkillLevelRestrictions().values().stream().findFirst().get();
                            return Integer.compare(itemVar1, itemVar2);
                        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            }
        } else {
            if (this.sortAlphabetical) {
                this.restrictions = this.restrictions.entrySet().stream()
                        .sorted((entry1, entry2) -> {
                            String itemName1 = BuiltInRegistries.BLOCK.byId(entry1.getKey()).getName().getString();
                            String itemName2 = BuiltInRegistries.BLOCK.byId(entry2.getKey()).getName().getString();
                            return itemName1.compareToIgnoreCase(itemName2);
                        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            } else {
                this.restrictions = this.restrictions.entrySet().stream()
                        .sorted((entry1, entry2) -> {
                            int itemVar1 = entry1.getValue().getSkillLevelRestrictions().values().stream().findFirst().get();
                            int itemVar2 = entry2.getValue().getSkillLevelRestrictions().values().stream().findFirst().get();
                            return Integer.compare(itemVar1, itemVar2);
                        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            }
        }
        this.lines.clear();

        Map<Integer, PlayerRestriction> newMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, PlayerRestriction> entry : this.restrictions.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
            if (newMap.size() == 9) {
                this.lines.add(new LineWidget(this.minecraft, null, newMap, code));
                newMap = new LinkedHashMap<>();
            }
        }
        if (!newMap.isEmpty()) {
            this.lines.add(new LineWidget(this.minecraft, null, newMap, code));
        }
        this.lineIndex = Math.max(0, Math.min(this.lineIndex, this.lines.size() - 10));
    }

    private void updateScrollFromMouse(double mouseY) {
        int maxRow = this.lines.size() - 10;
        this.lineIndex = LevelScreen.computeRowFromMouse(mouseY, this.y + 20, 187, 31, maxRow);
    }

}
