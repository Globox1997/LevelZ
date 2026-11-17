package net.levelz.screen;

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
import net.levelz.mixin.player.PlayerEntityAccessor;
import net.levelz.network.packet.AttributeSyncPacket;
import net.levelz.network.packet.StatPacket;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.player.ItemActivation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
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
    private LocalPlayer clientPlayerEntity;
    private final Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI).rotateLocalY(2.7f);
    private boolean turnClientPlayer = false;

    private final List<SkillAttribute> attributes = new ArrayList<>();
    private boolean showAttributes = false;
    private int attributeRow = 0;

    private final WidgetButtonPage[] levelButtons = new WidgetButtonPage[12];
    private int skillRow = 0;
    private final List<Integer> skillIds = new ArrayList<>();

    private boolean draggingSkillScrollbar = false;
    private boolean draggingAttributeScrollbar = false;

    public LevelScreen() {
        super(Component.translatable("screen.levelz.skill_screen"));
    }

    @Override
    protected void init() {
        super.init();
        ClientPlayNetworking.send(new AttributeSyncPacket());

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        this.levelManager = ((LevelManagerAccess) this.minecraft.player).getLevelManager();
        this.clientPlayerEntity = this.minecraft.gameMode.createPlayer(this.minecraft.level, this.minecraft.player.getStats(), this.minecraft.player.getRecipeBook(), new ItemActivation());
        this.clientPlayerEntity.setId(-1);
        ((ClientPlayerAccess) this.clientPlayerEntity).setShouldRenderClientName(false);
        byte playerModelParts = this.minecraft.player.getEntityData().get(PlayerEntityAccessor.getPLAYER_MODEL_PARTS());
        this.clientPlayerEntity.getEntityData().set(PlayerEntityAccessor.getPLAYER_MODEL_PARTS(), playerModelParts);

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (!this.minecraft.player.getItemBySlot(equipmentSlot).isEmpty()) {
                this.clientPlayerEntity.setItemSlot(equipmentSlot, this.minecraft.player.getItemBySlot(equipmentSlot));
            }
        }

        Map<Integer, SkillAttribute> skillAttributes = new TreeMap<>();
        for (Skill skill : LevelManager.SKILLS.values()) {
            for (SkillAttribute skillAttribute : skill.getAttributes()) {
                if (skillAttribute.getId() < 0) {
                    continue;
                }
                skillAttributes.put(skillAttribute.getId(), skillAttribute);
            }
        }
        this.attributes.clear();
        this.attributes.addAll(skillAttributes.values());

        this.skillIds.clear();
        this.skillIds.addAll(new TreeSet<>(LevelManager.SKILLS.keySet()));
        for (int i = 0; i < 12; i++) {
            if (this.skillIds.size() <= i) {
                break;
            }

            final int buttonIndex = i;
            this.levelButtons[i] = this.addRenderableWidget(new WidgetButtonPage(this.x + (i % 2 == 0 ? 80 : 169), this.y + 91 + i / 2 * 20, 13, 13, 33, 42, true, true, null, button -> {
                int skillIndex = this.skillRow * 2 + buttonIndex;
                if (skillIndex < this.skillIds.size()) {
                    ClientPlayNetworking.send(new StatPacket(this.skillIds.get(skillIndex), 1));
                }
            }));
        }
        updateLevelButtons();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);

        if (this.minecraft != null && this.minecraft.player != null) {
            Component title = Component.translatable("text.levelz.gui.title", this.minecraft.player.getName().getString());
            context.text(this.font, title, this.x + 118 - this.font.width(title) / 2, this.y + 7, 0xFF3F3F3F, false);

            if (!this.attributes.isEmpty()) {
                if (this.showAttributes) {
                    context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 5, 30, 114, 15, 13, 256, 256);
                    context.blit(RenderPipelines.GUI_TEXTURED, ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 202, this.y, 0, 0, 82, 215, 256, 256);
                    int maxAttributes = Math.min(this.attributes.size(), 15);
                    if (this.attributes.size() > 15) {
                        int scrollLevels = this.attributes.size() - 15;
                        int sliderY = this.attributeRow * 158 / scrollLevels;
                        context.blit(RenderPipelines.GUI_TEXTURED, ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 270, this.y + 8 + sliderY, 82, 0, 6, 41, 256, 256);
                    } else {
                        context.blit(RenderPipelines.GUI_TEXTURED, ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 270, this.y + 8, 88, 0, 6, 41, 256, 256);
                    }
                    context.text(this.font, Component.translatable("text.levelz.gui.attributes"), this.x + 214, this.y + 12, 0xFFE0E0E0, false);

                    int k = 27;
                    for (int i = this.attributeRow; i < this.attributeRow + maxAttributes; i++) {
                        String attributeKey = this.attributes.get(i).getAttibute().getRegisteredName();
                        if (attributeKey.contains(":")) {
                            attributeKey = attributeKey.split(":")[1];
                        }
                        context.blit(RenderPipelines.GUI_TEXTURED, LevelzMain.identifierOf("textures/gui/sprites/attribute/" + attributeKey + ".png"), this.x + 214, this.y + k, 0, 0, 9, 9, 9, 9);
                        float attributeValue = (float) Math.round(this.minecraft.player.getAttribute(this.attributes.get(i).getAttibute()).getValue() * 100.0D) / 100.0F;
                        context.text(this.font, Component.nullToEmpty(String.valueOf(attributeValue)), this.x + 214 + 15, this.y + k, 0xFFE0E0E0, false);

                        k += 12;
                    }
                } else {
                    context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 5, 15, 114, 15, 13, 256, 256);
                }
                if (isPointWithinBounds(this.x + 178, this.y + 5, 15, 13, mouseX, mouseY)) {
                    context.setTooltipForNextFrame(this.font, Component.translatable("text.levelz.gui.attributes"), mouseX, mouseY);
                }
            } else {
                context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 5, 0, 114, 15, 13, 256, 256);
            }

            // Level label
            Component skillLevelText = Component.translatable("text.levelz.gui.level", this.levelManager.getOverallLevel());
            context.text(this.font, skillLevelText, this.x + 62, this.y + 42, 0xFF3F3F3F, false);
            // Point label
            Component skillPointText = Component.translatable("text.levelz.gui.points", this.levelManager.getSkillPoints());
            context.text(this.font, skillPointText, this.x + 62, this.y + 54, 0xFF3F3F3F, false);

            // Experience bar
            context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 62, this.y + 21, 0, 100, 131, 5, 256, 256);

            int nextLevelExperience = this.levelManager.getNextLevelExperience();
            float levelProgress = this.levelManager.getLevelProgress();
            long experience = (int) (nextLevelExperience * levelProgress);

            context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 62, this.y + 21, 0, 105, (int) (130.0f * levelProgress), 5, 256, 256);
            // current xp label
            Component currentXpText = Component.translatable("text.levelz.gui.current_xp", experience, nextLevelExperience);
            context.text(this.font, currentXpText, this.x - this.font.width(currentXpText) / 2 + 127, this.y + 30, 0xFF3F3F3F, false);

            if (!LevelManager.CRAFTING_RESTRICTIONS.isEmpty()) {
                if (isPointWithinBounds(this.x + 178, this.y + 29, 14, 13, mouseX, mouseY)) {
                    context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 29, 30, 80, 15, 13, 256, 256);
                    context.setTooltipForNextFrame(this.font, Component.translatable("restriction.levelz.crafting"), mouseX, mouseY);
                } else {
                    context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 29, 15, 80, 15, 13, 256, 256);
                }
            } else {
                context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 29, 0, 80, 15, 13, 256, 256);
            }

            if (!LevelManager.MINING_RESTRICTIONS.isEmpty()) {
                if (isPointWithinBounds(this.x + 178, this.y + 45, 14, 13, mouseX, mouseY)) {
                    context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 45, 75, 80, 15, 13, 256, 256);
                    context.setTooltipForNextFrame(this.font, Component.translatable("restriction.levelz.mining"), mouseX, mouseY);
                } else {
                    context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 45, 60, 80, 15, 13, 256, 256);
                }
            } else {
                context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 178, this.y + 45, 45, 80, 15, 13, 256, 256);
            }
        }

        if (this.clientPlayerEntity != null) {
            EntityRenderState renderState = this.minecraft.getEntityRenderDispatcher().getRenderer(this.clientPlayerEntity).createRenderState(this.clientPlayerEntity, 1.0F);
            renderState.shadowPieces.clear();
            renderState.outlineColor = 0;
            context.entity(renderState, 30f, new Vector3f(0.0F, renderState.boundingBoxHeight / 2.0F, 0.0F), this.quaternionf, null, this.x + 8, this.y + 8, this.x + 58, this.y + 78);

            if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY)) {
                context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 9, this.y + 67, 0, 138, 15, 10, 256, 256);
            } else {
                context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 9, this.y + 67, 0, 128, 15, 10, 256, 256);
            }
            if (isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
                context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 41, this.y + 67, 15, 138, 15, 10, 256, 256);
            } else {
                context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.x + 41, this.y + 67, 15, 128, 15, 10, 256, 256);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        extractTransparentBackground(context);
        context.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);

        for (int i = 0; i < 12; i++) {
            int skillIndex = i + this.skillRow * 2;
            if (this.skillIds.size() <= skillIndex) {
                break;
            }
            int skillId = this.skillIds.get(skillIndex);
            context.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.x + (i % 2 == 0 ? 8 : 96), this.y + 87 + i / 2 * 20, 0, 215, 88, 20, 256, 256);
            context.blit(RenderPipelines.GUI_TEXTURED, LevelzMain.identifierOf("textures/gui/sprites/" + LevelManager.SKILLS.get(skillId).getKey() + ".png"), this.x + (i % 2 == 0 ? 11 : 99), this.y + 89 + i / 2 * 20, 0, 0, 16, 16, 16, 16);

            Component skillLevel = Component.translatable("text.levelz.gui.current_level", this.levelManager.getSkillLevel(skillId), LevelManager.SKILLS.get(skillId).getMaxLevel());
            context.text(this.font, skillLevel, this.x + (i % 2 == 0 ? 53 : 141) - this.font.width(skillLevel) / 2, this.y + 94 + i / 2 * 20, 0xFF3F3F3F, false);

            if (isPointWithinBounds(this.x + (i % 2 == 0 ? 11 : 99), this.y + 89 + i / 2 * 20, 16, 16, mouseX, mouseY)) {
                context.setTooltipForNextFrame(this.font, LevelManager.SKILLS.get(skillId).getText(), mouseX, mouseY);
            }
        }
        if (this.skillIds.size() > 12) {
            int scrollLevels = (this.skillIds.size() - 12) / 2;
            if (this.skillIds.size() % 2 != 0) {
                scrollLevels += 1;
            }

            int sliderY = this.skillRow * 86 / scrollLevels;
            context.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.x + 186, this.y + 87 + sliderY, 200, 0, 6, 34, 256, 256);
        } else {
            context.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.x + 186, this.y + 87, 206, 0, 6, 34, 256, 256);
        }
        DrawTabHelper.drawTab(minecraft, context, this, this.x, this.y, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.clientPlayerEntity != null && this.turnClientPlayer) {
            double mouseX = this.minecraft.mouseHandler.xpos() * (double) this.minecraft.getWindow().getGuiScaledWidth() / (double) this.minecraft.getWindow().getScreenWidth();
            double mouseY = this.minecraft.mouseHandler.ypos() * (double) this.minecraft.getWindow().getGuiScaledHeight() / (double) this.minecraft.getWindow().getScreenHeight();
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
    public boolean keyPressed(KeyEvent event) {
        if (KeyInit.screenKey.matches(event) || Objects.requireNonNull(minecraft).options.keyInventory.matches(event)) {
            this.onClose();
            return true;
        } else {
            return super.keyPressed(event);
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.turnClientPlayer) {
            this.turnClientPlayer = false;
        }
        this.draggingSkillScrollbar = false;
        this.draggingAttributeScrollbar = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        DrawTabHelper.onTabButtonClick(minecraft, this, this.x, this.y, mouseX, mouseY, this.getFocused() != null);

        if (this.skillIds.size() > 12 && isPointWithinBounds(this.x + 186, this.y + 87, 6, 120, mouseX, mouseY)) {
            this.draggingSkillScrollbar = true;
            updateSkillScrollFromMouse(mouseY);
            return true;
        }
        if (this.showAttributes && this.attributes.size() > 15 && isPointWithinBounds(this.x + 270, this.y + 8, 6, 199, mouseX, mouseY)) {
            this.draggingAttributeScrollbar = true;
            updateAttributeScrollFromMouse(mouseY);
            return true;
        }
        if (!this.attributes.isEmpty() && isPointWithinBounds(this.x + 178, this.y + 5, 15, 13, mouseX, mouseY)) {
            this.showAttributes = !this.showAttributes;
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        if (!LevelManager.CRAFTING_RESTRICTIONS.isEmpty() && isPointWithinBounds(this.x + 178, this.y + 29, 14, 13, mouseX, mouseY)) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gui.setScreen(new SkillRestrictionScreen(this.levelManager, LevelManager.CRAFTING_RESTRICTIONS, Component.translatable("restriction.levelz.crafting"), 0));
            return true;
        }
        if (!LevelManager.MINING_RESTRICTIONS.isEmpty() && isPointWithinBounds(this.x + 178, this.y + 45, 14, 13, mouseX, mouseY)) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gui.setScreen(new SkillRestrictionScreen(this.levelManager, LevelManager.MINING_RESTRICTIONS, Component.translatable("restriction.levelz.mining"), 1));
            return true;
        }
        if (this.clientPlayerEntity != null) {
            if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.turnClientPlayer = true;
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else if (isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
                this.turnClientPlayer = true;
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        for (int i = 0; i < 12; i++) {
            int skillIndex = i + this.skillRow * 2;
            if (this.skillIds.size() <= skillIndex) {
                break;
            }
            int skillId = this.skillIds.get(skillIndex);
            if (isPointWithinBounds(this.x + (i % 2 == 0 ? 11 : 99), this.y + 89 + i / 2 * 20, 16, 16, mouseX, mouseY)) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.minecraft.gui.setScreen(new SkillInfoScreen(this.levelManager, skillId));
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
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
        if (this.skillIds.size() > 12 && isPointWithinBounds(this.x + 7, this.y + 86, 186, 122, mouseX, mouseY)) {

            int maxSkillRow = (this.skillIds.size() - 12) / 2;
            if (this.skillIds.size() % 2 != 0) {
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
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        double mouseY = event.y();
        if (this.draggingSkillScrollbar) {
            updateSkillScrollFromMouse(mouseY);
            return true;
        }
        if (this.draggingAttributeScrollbar) {
            updateAttributeScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void updateLevelButtons() {
        for (int i = 0; i < this.levelButtons.length; i++) {
            if (this.levelButtons[i] == null) {
                break;
            }
            int skillIndex = i + this.skillRow * 2;
            if (this.skillIds.size() <= skillIndex) {
                this.levelButtons[i].visible = false;
                continue;
            } else {
                this.levelButtons[i].visible = true;
            }
            int skillId = this.skillIds.get(skillIndex);

            if (LevelManager.SKILLS.get(skillId).getMaxLevel() <= this.levelManager.getSkillLevel(skillId)) {
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

    public static int computeRowFromMouse(double mouseY, int trackY, int trackHeight, int thumbHeight, int maxRow) {
        if (maxRow <= 0) {
            return 0;
        }
        int usableTrack = trackHeight - thumbHeight;
        if (usableTrack <= 0) {
            return 0;
        }
        double relativeY = mouseY - trackY - thumbHeight / 2.0;
        int row = (int) Math.round(relativeY / usableTrack * maxRow);
        return Math.max(0, Math.min(maxRow, row));
    }

    private void updateSkillScrollFromMouse(double mouseY) {
        int maxSkillRow = (this.skillIds.size() - 12) / 2;
        if (this.skillIds.size() % 2 != 0) {
            maxSkillRow += 1;
        }
        int oldSkillRow = this.skillRow;
        this.skillRow = computeRowFromMouse(mouseY, this.y + 87, 120, 34, maxSkillRow);
        if (oldSkillRow != this.skillRow) {
            updateLevelButtons();
        }
    }

    private void updateAttributeScrollFromMouse(double mouseY) {
        int maxAttributeRow = this.attributes.size() - 15;
        this.attributeRow = computeRowFromMouse(mouseY, this.y + 8, 199, 41, maxAttributeRow);
    }

    private static class WidgetButtonPage extends Button {

        private final boolean hoverOutline;
        private final boolean clickable;
        private final int textureX;
        private final int textureY;
        private List<Component> tooltip = new ArrayList<Component>();
        private int clickedKey = -1;

        public WidgetButtonPage(int x, int y, int sizeX, int sizeY, int textureX, int textureY, boolean hoverOutline, boolean clickable, @Nullable Component tooltip, Button.OnPress onPress) {
            super(x, y, sizeX, sizeY, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
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
        protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
            Minecraft minecraftClient = Minecraft.getInstance();
            int i = hoverOutline ? this.getTextureY() : 0;
            context.blit(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, this.getX(), this.getY(), this.textureX + i * this.width, this.textureY, this.width, this.height, 256, 256, ARGB.white(this.alpha));
            if (this.isHovered()) {
                context.setComponentTooltipForNextFrame(minecraftClient.font, this.tooltip, mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            this.clickedKey = event.button();
            if (!this.clickable) {
                return false;
            }
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
            return super.isValidClickButton(buttonInfo) || buttonInfo.button() == 1 || buttonInfo.button() == 2;
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (!this.clickable) {
                return false;
            }
            return super.keyPressed(event);
        }

        public void addTooltip(Component text) {
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
