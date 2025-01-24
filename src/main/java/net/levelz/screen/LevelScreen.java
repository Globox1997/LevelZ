package net.levelz.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.levelz.LevelzMain;
import net.levelz.access.ClientPlayerAccess;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.level.SkillAttribute;
import net.levelz.network.packet.AttributeSyncPacket;
import net.levelz.network.packet.StatPacket;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

@Environment(EnvType.CLIENT)
public class LevelScreen extends Screen implements Tab {

    public static final Identifier BACKGROUND_TEXTURE = LevelzMain.identifierOf("textures/gui/skill_background.png");
    public static final Identifier ATTRIBUTE_BACKGROUND_TEXTURE = LevelzMain.identifierOf("textures/gui/attribute_background.png");
    public static final Identifier ICON_TEXTURE = LevelzMain.identifierOf("textures/gui/icons.png");

    private final int backgroundWidth = 200;
    private final int backgroundHeight = 215;
    private int x;
    private int y;

    private LevelManager levelManager;
    private ClientPlayerEntity clientPlayerEntity;
    private Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI).rotateLocalY(2.7f);
    private boolean turnClientPlayer = false;

    private List<SkillAttribute> attributes = new ArrayList<>();
    private boolean showAttributes = false;
    private int attributeRow = 0;

    private final WidgetButtonPage[] levelButtons = new WidgetButtonPage[12];
    private int skillRow = 0;

    public LevelScreen() {
        super(Text.translatable("screen.levelz.skill_screen"));
    }

    @Override
    protected void init() {
        super.init();
        ClientPlayNetworking.send(new AttributeSyncPacket());

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        this.levelManager = ((LevelManagerAccess) this.client.player).getLevelManager();
        this.clientPlayerEntity = this.client.interactionManager.createPlayer(this.client.world, this.client.player.getStatHandler(), this.client.player.getRecipeBook(), false, false);
        ((ClientPlayerAccess) this.clientPlayerEntity).setShouldRenderClientName(false);

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (!this.client.player.getEquippedStack(equipmentSlot).isEmpty()) {
                this.clientPlayerEntity.equipStack(equipmentSlot, this.client.player.getEquippedStack(equipmentSlot));
            }
        }

        Map<Integer, SkillAttribute> skillAttributes = new HashMap<>();
        int attributeCount = 0;
        for (Skill skill : LevelManager.SKILLS.values()) {
            for (SkillAttribute skillAttribute : skill.getAttributes()) {
                if (skillAttribute.getId() < 0) {
                    continue;
                }
                skillAttributes.put(skillAttribute.getId(), skillAttribute);
                attributeCount++;

            }
        }
        for (int i = 0; i < attributeCount; i++) {
            this.attributes.add(skillAttributes.get(i));
        }
        for (int i = 0; i < 12; i++) {
            if (this.levelManager.getPlayerSkills().size() <= i) {
                break;
            }

            final int skillId = i;
            this.levelButtons[i] = this.addDrawableChild(new WidgetButtonPage(this.x + (i % 2 == 0 ? 80 : 169), this.y + 91 + i / 2 * 20, 13, 13, 33, 42, true, true, null, button -> {
                ClientPlayNetworking.send(new StatPacket(this.skillRow * 2 + skillId, 1));
            }));
        }
        updateLevelButtons();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (this.client != null && this.client.player != null) {
            Text title = Text.translatable("text.levelz.gui.title", this.client.player.getName().getString());
            context.drawText(this.textRenderer, title, this.x + 118 - this.textRenderer.getWidth(title) / 2, this.y + 7, 0x3F3F3F, false);

            if (!this.attributes.isEmpty()) {
                if (this.showAttributes) {
                    context.drawTexture(ICON_TEXTURE, this.x + 178, this.y + 5, 30, 114, 15, 13);
                    context.drawTexture(ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 202, this.y, 0, 0, 82, 215);
                    int maxAttributes = Math.min(this.attributes.size(), 15);
                    if (this.attributes.size() > 15) {
                        int scrollLevels = this.attributes.size() - 15;
                        int sliderY = this.attributeRow * 158 / scrollLevels;
                        context.drawTexture(ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 270, this.y + 8 + sliderY, 82, 0, 6, 41);
                    } else {
                        context.drawTexture(ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 270, this.y + 8, 88, 0, 6, 41);
                    }
                    context.drawText(this.textRenderer, Text.translatable("text.levelz.gui.attributes"), this.x + 214, this.y + 12, 0xE0E0E0, false);

                    int k = 27;
                    for (int i = this.attributeRow; i < this.attributeRow + maxAttributes; i++) {
                        String attributeKey = this.attributes.get(i).getAttibute().getIdAsString();
                        if (attributeKey.contains(":")) {
                            attributeKey = attributeKey.split(":")[1];
                        }
                        context.drawTexture(LevelzMain.identifierOf("textures/gui/sprites/" + attributeKey + ".png"), this.x + 214, this.y + k, 0, 0, 9, 9, 9, 9);
                        float attributeValue = (float) Math.round(this.client.player.getAttributeInstance(this.attributes.get(i).getAttibute()).getValue() * 100.0D) / 100.0F;
                        context.drawText(this.textRenderer, Text.of(String.valueOf(attributeValue)), this.x + 214 + 15, this.y + k, 0xE0E0E0, false);

                        k += 12;
                    }
                } else {
                    context.drawTexture(ICON_TEXTURE, this.x + 178, this.y + 5, 15, 114, 15, 13);
                }
                if (isPointWithinBounds(this.x + 178, this.y + 5, 15, 13, mouseX, mouseY)) {
                    context.drawTooltip(this.textRenderer, Text.translatable("text.levelz.gui.attributes"), mouseX, mouseY);
                }
            } else {
                context.drawTexture(ICON_TEXTURE, this.x + 178, this.y + 5, 0, 114, 15, 13);
            }

            // Level label
            Text skillLevelText = Text.translatable("text.levelz.gui.level", this.levelManager.getOverallLevel());
            context.drawText(this.textRenderer, skillLevelText, this.x + 62, this.y + 42, 0x3F3F3F, false);
            // Point label
            Text skillPointText = Text.translatable("text.levelz.gui.points", this.levelManager.getSkillPoints());
            context.drawText(this.textRenderer, skillPointText, this.x + 62, this.y + 54, 0x3F3F3F, false);

            // Experience bar
            context.drawTexture(ICON_TEXTURE, this.x + 62, this.y + 21, 0, 100, 131, 5);

            int nextLevelExperience = this.levelManager.getNextLevelExperience();
            float levelProgress = this.levelManager.getLevelProgress();
            long experience = (int) (nextLevelExperience * levelProgress);

            context.drawTexture(ICON_TEXTURE, this.x + 62, this.y + 21, 0, 105, (int) (130.0f * levelProgress), 5);
            // current xp label
            Text currentXpText = Text.translatable("text.levelz.gui.current_xp", experience, nextLevelExperience);
            context.drawText(this.textRenderer, currentXpText, this.x - this.textRenderer.getWidth(currentXpText) / 2 + 127, this.y + 30, 0x3F3F3F, false);

            if (!LevelManager.CRAFTING_RESTRICTIONS.isEmpty()) {
                if (isPointWithinBounds(this.x + 178, this.y + 29, 14, 13, mouseX, mouseY)) {
                    context.drawTexture(ICON_TEXTURE, this.x + 178, this.y + 29, 30, 80, 15, 13);
                    context.drawTooltip(this.textRenderer, Text.translatable("restriction.levelz.crafting"), mouseX, mouseY);
                } else {
                    context.drawTexture(ICON_TEXTURE, this.x + 178, this.y + 29, 15, 80, 15, 13);
                }
            } else {
                context.drawTexture(ICON_TEXTURE, this.x + 178, this.y + 29, 0, 80, 15, 13);
            }

            if (!LevelManager.MINING_RESTRICTIONS.isEmpty()) {
                if (isPointWithinBounds(this.x + 178, this.y + 45, 14, 13, mouseX, mouseY)) {
                    context.drawTexture(ICON_TEXTURE, this.x + 178, this.y + 45, 75, 80, 15, 13);
                    context.drawTooltip(this.textRenderer, Text.translatable("restriction.levelz.mining"), mouseX, mouseY);
                } else {
                    context.drawTexture(ICON_TEXTURE, this.x + 178, this.y + 45, 60, 80, 15, 13);
                }
            } else {
                context.drawTexture(ICON_TEXTURE, this.x + 178, this.y + 45, 45, 80, 15, 13);
            }
        }

        if (this.clientPlayerEntity != null) {
            InventoryScreen.drawEntity(context, this.x + 33, this.y + 43, 30f, new Vector3f(0.0F, this.clientPlayerEntity.getHeight() / 2.0F, 0.0F), this.quaternionf, null, this.clientPlayerEntity);

            if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY)) {
                context.drawTexture(ICON_TEXTURE, this.x + 9, this.y + 67, 0, 138, 15, 10);
            } else {
                context.drawTexture(ICON_TEXTURE, this.x + 9, this.y + 67, 0, 128, 15, 10);
            }
            if (isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
                context.drawTexture(ICON_TEXTURE, this.x + 41, this.y + 67, 15, 138, 15, 10);
            } else {
                context.drawTexture(ICON_TEXTURE, this.x + 41, this.y + 67, 15, 128, 15, 10);
            }
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderInGameBackground(context);
        context.drawTexture(BACKGROUND_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        for (int i = 0; i < 12; i++) {
            int skillId = i + this.skillRow * 2;
            if (LevelManager.SKILLS.size() <= skillId) {
                break;
            }
            if (this.levelManager.getPlayerSkills().size() <= skillId) {
                break;
            }
            context.drawTexture(BACKGROUND_TEXTURE, this.x + (i % 2 == 0 ? 8 : 96), this.y + 87 + i / 2 * 20, 0, 215, 88, 20);
            context.drawTexture(LevelzMain.identifierOf("textures/gui/sprites/" + LevelManager.SKILLS.get(skillId).getKey() + ".png"), this.x + (i % 2 == 0 ? 11 : 99), this.y + 89 + i / 2 * 20, 0, 0, 16, 16, 16, 16);

            Text skillLevel = Text.translatable("text.levelz.gui.current_level", this.levelManager.getSkillLevel(skillId), LevelManager.SKILLS.get(skillId).getMaxLevel());
            context.drawText(this.textRenderer, skillLevel, this.x + (i % 2 == 0 ? 53 : 141) - this.textRenderer.getWidth(skillLevel) / 2, this.y + 94 + i / 2 * 20, 0x3F3F3F, false);

            if (isPointWithinBounds(this.x + (i % 2 == 0 ? 11 : 99), this.y + 89 + i / 2 * 20, 16, 16, mouseX, mouseY)) {
                context.drawTooltip(this.textRenderer, LevelManager.SKILLS.get(skillId).getText(), mouseX, mouseY);
            }
        }
        if (this.levelManager.getPlayerSkills().size() > 12) {
            int scrollLevels = (this.levelManager.getPlayerSkills().size() - 12) / 2;
            if (this.levelManager.getPlayerSkills().size() % 2 != 0) {
                scrollLevels += 1;
            }

            int sliderY = this.skillRow * 86 / scrollLevels;
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 186, this.y + 87 + sliderY, 200, 0, 6, 34);
        } else {
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 186, this.y + 87, 206, 0, 6, 34);
        }
        DrawTabHelper.drawTab(client, context, this, this.x, this.y, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.clientPlayerEntity != null && this.turnClientPlayer) {
            double mouseX = this.client.mouse.getX() * (double) this.client.getWindow().getScaledWidth() / (double) this.client.getWindow().getWidth();
            double mouseY = this.client.mouse.getY() * (double) this.client.getWindow().getScaledHeight() / (double) this.client.getWindow().getHeight();
            if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.quaternionf.rotateLocalY(0.087f);
            } else if (isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.quaternionf.rotateLocalY(-0.087f);
            } else {
                this.turnClientPlayer = false;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyInit.screenKey.matchesKey(keyCode, scanCode) || Objects.requireNonNull(client).options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.turnClientPlayer) {
            this.turnClientPlayer = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        DrawTabHelper.onTabButtonClick(client, this, this.x, this.y, mouseX, mouseY, this.getFocused() != null);

        if (!this.attributes.isEmpty() && isPointWithinBounds(this.x + 178, this.y + 5, 15, 13, mouseX, mouseY)) {
            this.showAttributes = !this.showAttributes;
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        if (!LevelManager.CRAFTING_RESTRICTIONS.isEmpty() && isPointWithinBounds(this.x + 178, this.y + 29, 14, 13, mouseX, mouseY)) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.client.setScreen(new SkillRestrictionScreen(this.levelManager, LevelManager.CRAFTING_RESTRICTIONS, Text.translatable("restriction.levelz.crafting"), 0));
            return true;
        }
        if (!LevelManager.MINING_RESTRICTIONS.isEmpty() && isPointWithinBounds(this.x + 178, this.y + 45, 14, 13, mouseX, mouseY)) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.client.setScreen(new SkillRestrictionScreen(this.levelManager, LevelManager.MINING_RESTRICTIONS, Text.translatable("restriction.levelz.mining"), 1));
            return true;
        }
        if (this.clientPlayerEntity != null) {
            if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.turnClientPlayer = true;
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else if (isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.turnClientPlayer = true;
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        for (int i = 0; i < 12; i++) {
            int skillId = i + this.skillRow * 2;
            if (LevelManager.SKILLS.size() <= skillId) {
                break;
            }
            if (this.levelManager.getPlayerSkills().size() <= skillId) {
                break;
            }
            if (isPointWithinBounds(this.x + (i % 2 == 0 ? 11 : 99), this.y + 89 + i / 2 * 20, 16, 16, mouseX, mouseY)) {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.client.setScreen(new SkillInfoScreen(this.levelManager, skillId));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.showAttributes && this.attributes.size() > 15 && isPointWithinBounds(this.x + 209, this.y + 7, 68, 201, mouseX, mouseY)) {
            int maxAttributeRow = this.attributes.size() - 15;
            int newAttributeRow = this.attributeRow;
            newAttributeRow = newAttributeRow - (int) (verticalAmount);
            if (newAttributeRow < 0) {
                this.attributeRow = 0;
            } else {
                this.attributeRow = Math.min(newAttributeRow, maxAttributeRow);
            }
        }
        if (this.levelManager.getPlayerSkills().size() > 12 && isPointWithinBounds(this.x + 7, this.y + 86, 186, 122, mouseX, mouseY)) {

            int maxSkillRow = (this.levelManager.getPlayerSkills().size() - 12) / 2;
            if (this.levelManager.getPlayerSkills().size() % 2 != 0) {
                maxSkillRow += 1;
            }
            int oldSkillRow = this.skillRow;
            int newSkillRow = this.skillRow;
            newSkillRow = newSkillRow - (int) (verticalAmount);
            if (newSkillRow < 0) {
                this.skillRow = 0;
            } else {
                this.skillRow = Math.min(newSkillRow, maxSkillRow);
            }
            if (oldSkillRow != this.skillRow) {
                updateLevelButtons();
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void updateLevelButtons() {
        for (int i = 0; i < this.levelButtons.length; i++) {
            if (this.levelManager.getPlayerSkills().size() <= i) {
                break;
            }
            int skillId = i + this.skillRow * 2;
            if (LevelManager.SKILLS.size() <= skillId) {
                this.levelButtons[i].visible = false;
                return;
            } else {
                this.levelButtons[i].visible = true;
            }


            if (ConfigInit.CONFIG.overallMaxLevel > 0 && this.levelManager.getOverallLevel() >= ConfigInit.CONFIG.overallMaxLevel) {
                this.levelButtons[i].active = false;
            } else if (LevelManager.SKILLS.get(skillId).getMaxLevel() <= this.levelManager.getPlayerSkills().get(skillId).getLevel()) {
                this.levelButtons[i].active = false;
            } else {
                this.levelButtons[i].active = this.levelManager.getSkillPoints() > 0;
            }
            if (ConfigInit.CONFIG.allowHigherSkillLevel && this.levelManager.getSkillPoints() > 0) {
                boolean maxedAllSkills = true;
                for (Skill skillCheck : LevelManager.SKILLS.values()) {
                    if (skillCheck.getMaxLevel() > this.levelManager.getSkillLevel(skillCheck.getId())) {
                        maxedAllSkills = false;
                        break;
                    }
                }
                if (maxedAllSkills) {
                    this.levelButtons[i].active = true;
                }
            }
        }
    }

    public static boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        return pointX >= (double) (x - 1) && pointX < (double) (x + width + 1) && pointY >= (double) (y - 1) && pointY < (double) (y + height + 1);
    }

    private static class WidgetButtonPage extends ButtonWidget {

        private final boolean hoverOutline;
        private final boolean clickable;
        private final int textureX;
        private final int textureY;
        private List<Text> tooltip = new ArrayList<Text>();
        private int clickedKey = -1;

        public WidgetButtonPage(int x, int y, int sizeX, int sizeY, int textureX, int textureY, boolean hoverOutline, boolean clickable, @Nullable Text tooltip, ButtonWidget.PressAction onPress) {
            super(x, y, sizeX, sizeY, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.hoverOutline = hoverOutline;
            this.clickable = clickable;
            this.textureX = textureX;
            this.textureY = textureY;
            this.width = sizeX;
            this.height = sizeY;
            if (tooltip != null) {
                this.tooltip.add(tooltip);
            }
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            int i = hoverOutline ? this.getTextureY() : 0;
            context.drawTexture(ICON_TEXTURE, this.getX(), this.getY(), this.textureX + i * this.width, this.textureY, this.width, this.height);
            context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            if (this.isHovered()) {
                context.drawTooltip(minecraftClient.textRenderer, this.tooltip, mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.clickedKey = button;
            if (!this.clickable) {
                return false;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        protected boolean isValidClickButton(int button) {
            return super.isValidClickButton(button) || button == 1 || button == 2;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!this.clickable) {
                return false;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        public void addTooltip(Text text) {
            this.tooltip.add(text);
        }

        public boolean wasMiddleButtonClicked() {
            return clickedKey == 2;
        }

        public boolean wasRightButtonClicked() {
            return clickedKey == 1;
        }

        private int getTextureY() {
            int i = 1;
            if (!this.active) {
                i = 0;
            } else if (this.isHovered()) {
                i = 2;
            }
            return i;
        }
    }
}
