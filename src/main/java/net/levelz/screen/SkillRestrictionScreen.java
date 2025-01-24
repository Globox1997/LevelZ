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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
    private final Text title;
    private final int code;

    private int lineIndex = 0;
    private boolean sortAlphabetical = false;

    public SkillRestrictionScreen(LevelManager levelManager, Map<Integer, PlayerRestriction> restrictions, Text title, int code) {
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
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawText(this.textRenderer, this.title, this.x + 7, this.y + 7, 0x3F3F3F, false);

        for (int i = 0; i < 10; i++) {
            if (this.lines.size() <= i) {
                break;
            }
            int index = this.lineIndex + i;

            this.lines.get(index).render(context, this.x + 12, this.y + 24 + i * 18, mouseX, mouseY);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderInGameBackground(context);
        context.drawTexture(BACKGROUND_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);

        if (this.lines.size() > 10) {
            int scrollLevels = this.lines.size() - 10;
            int sliderY = this.lineIndex * 156 / scrollLevels;
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 186, this.y + 20 + sliderY, 200, 0, 6, 31);
        } else {
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 186, this.y + 20, 206, 0, 6, 31);
        }
        int sortU = LevelScreen.isPointWithinBounds(this.x + 179, this.y + 4, 14, 14, mouseX, mouseY) ? 14 : 0;
        int sortV = this.sortAlphabetical ? 180 : 166;
        context.drawTexture(LevelScreen.ICON_TEXTURE, this.x + 179, this.y + 4, sortU, sortV, 14, 14);

        DrawTabHelper.drawTab(client, context, this, this.x, this.y, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            if (ConfigInit.CONFIG.switchScreen) {
                this.client.setScreen(new LevelScreen());
            } else {
                this.close();
            }
            return true;

        } else if (KeyInit.screenKey.matchesKey(keyCode, scanCode)) {
            this.client.setScreen(new LevelScreen());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        DrawTabHelper.onTabButtonClick(client, this, this.x, this.y, mouseX, mouseY, false);
        if (LevelScreen.isPointWithinBounds(this.x + 179, this.y + 4, 14, 14, mouseX, mouseY)) {
            this.sortAlphabetical = !this.sortAlphabetical;
            sortRestrictions();
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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

    private void sortRestrictions() {
        if (this.code == 0) {
            if (this.sortAlphabetical) {
                this.restrictions = this.restrictions.entrySet().stream()
                        .sorted((entry1, entry2) -> {
                            String itemName1 = Registries.ITEM.get(entry1.getKey()).getName().getString();
                            String itemName2 = Registries.ITEM.get(entry2.getKey()).getName().getString();
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
                            String itemName1 = Registries.BLOCK.get(entry1.getKey()).getName().getString();
                            String itemName2 = Registries.BLOCK.get(entry2.getKey()).getName().getString();
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

        int count = 0;
        Map<Integer, PlayerRestriction> newMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, PlayerRestriction> entry : this.restrictions.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
            count++;
            if (count == this.restrictions.size() - 1) {
                this.lines.add(new LineWidget(this.client, null, newMap, code));
                break;
            }
            if (count != 0 && count % 9 == 0) {
                this.lines.add(new LineWidget(this.client, null, new LinkedHashMap<>(newMap), code));
                newMap.clear();
            }

        }
    }

}
