package net.levelz.screen;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.screen.widget.SkillScrollableWidget;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkillInfoScreen extends Screen implements Tab {

    public static final Identifier BACKGROUND_TEXTURE = new Identifier("levelz:textures/gui/skill_info_background.png");

    private int backgroundWidth = 200;
    private int backgroundHeight = 215;
    private int x;
    private int y;

    private final String title;

    private Text translatableText1A = null;
    private Text translatableText1B = null;
    private Text translatableText2A = null;
    private Text translatableText2B = null;
    private Text translatableText3A = null;
    private Text translatableText3B = null;
    private Text translatableText6A = null;
    private Text translatableText6B = null;

    public SkillInfoScreen(String title) {
        super(Text.of(title));
        this.title = title;
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        switch (this.title) {
        case "health":
            this.translatableText1A = Text.translatable("text.levelz.health_info_1", ConfigInit.CONFIG.healthBase);
            this.translatableText2A = Text.translatable("text.levelz.health_info_2_1", ConfigInit.CONFIG.healthBonus);
            this.translatableText2B = Text.translatable("text.levelz.health_info_2_2", ConfigInit.CONFIG.healthBonus);
            this.translatableText6A = Text.translatable("text.levelz.health_max_lvl_1", ConfigInit.CONFIG.healthAbsorptionBonus);
            this.translatableText6B = Text.translatable("text.levelz.health_max_lvl_2", ConfigInit.CONFIG.healthAbsorptionBonus);
            break;
        case "strength":
            this.translatableText1A = Text.translatable("text.levelz.strength_info_1", ConfigInit.CONFIG.attackBase);
            this.translatableText2A = Text.translatable("text.levelz.strength_info_2_1", ConfigInit.CONFIG.attackBonus);
            this.translatableText2B = Text.translatable("text.levelz.strength_info_2_2", ConfigInit.CONFIG.attackBonus);
            this.translatableText6A = Text.translatable("text.levelz.strength_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.strength_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F));
            break;
        case "agility":
            this.translatableText1A = Text.translatable("text.levelz.agility_info_1", ConfigInit.CONFIG.movementBase);
            this.translatableText2A = Text.translatable("text.levelz.agility_info_2_1", ConfigInit.CONFIG.movementBonus);
            this.translatableText2B = Text.translatable("text.levelz.agility_info_2_2", ConfigInit.CONFIG.movementBonus);
            this.translatableText3A = Text.translatable("text.levelz.agility_info_3_1", ConfigInit.CONFIG.movementFallBonus);
            this.translatableText3B = Text.translatable("text.levelz.agility_info_3_2", ConfigInit.CONFIG.movementFallBonus);
            this.translatableText6A = Text.translatable("text.levelz.agility_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.movementMissChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.agility_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.movementMissChance * 100F));
            break;
        case "defense":
            this.translatableText1A = Text.translatable("text.levelz.defense_info_1", ConfigInit.CONFIG.defenseBase);
            this.translatableText2A = Text.translatable("text.levelz.defense_info_2_1", ConfigInit.CONFIG.defenseBonus);
            this.translatableText2B = Text.translatable("text.levelz.defense_info_2_2", ConfigInit.CONFIG.defenseBonus);
            this.translatableText6A = Text.translatable("text.levelz.defense_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.defenseReflectChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.defense_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.defenseReflectChance * 100F));
            break;
        case "stamina":
            this.translatableText1A = Text.translatable("text.levelz.stamina_info_1", ConfigInit.CONFIG.staminaBase);
            this.translatableText2A = Text.translatable("text.levelz.stamina_info_2_1", ConfigInit.CONFIG.staminaBonus);
            this.translatableText2B = Text.translatable("text.levelz.stamina_info_2_2", ConfigInit.CONFIG.staminaBonus);
            this.translatableText3A = Text.translatable("text.levelz.stamina_info_3_1", ConfigInit.CONFIG.staminaHealthBonus);
            this.translatableText3B = Text.translatable("text.levelz.stamina_info_3_2", ConfigInit.CONFIG.staminaHealthBonus);
            this.translatableText6A = Text.translatable("text.levelz.stamina_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.staminaFoodBonus * 100F));
            this.translatableText6B = Text.translatable("text.levelz.stamina_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.staminaFoodBonus * 100F));
            break;
        case "luck":
            this.translatableText1A = Text.translatable("text.levelz.luck_info_1", ConfigInit.CONFIG.luckBase);
            this.translatableText1B = Text.translatable("text.levelz.luck_info_1_2");
            this.translatableText2A = Text.translatable("text.levelz.luck_info_2_1", ConfigInit.CONFIG.luckBonus);
            this.translatableText2B = Text.translatable("text.levelz.luck_info_2_2", ConfigInit.CONFIG.luckBonus);
            this.translatableText3A = Text.translatable("text.levelz.luck_info_3_1", ConfigInit.CONFIG.luckCritBonus);
            this.translatableText3B = Text.translatable("text.levelz.luck_info_3_2", ConfigInit.CONFIG.luckCritBonus);
            this.translatableText6A = Text.translatable("text.levelz.luck_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.luckSurviveChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.luck_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.luckSurviveChance * 100F));
            break;
        case "archery":
            this.translatableText2A = Text.translatable("text.levelz.archery_info_2_1", ConfigInit.CONFIG.archeryBowExtraDamage);
            this.translatableText2B = Text.translatable("text.levelz.archery_info_2_2", ConfigInit.CONFIG.archeryBowExtraDamage);
            this.translatableText3A = Text.translatable("text.levelz.archery_info_3_1", ConfigInit.CONFIG.archeryCrossbowExtraDamage);
            this.translatableText3B = Text.translatable("text.levelz.archery_info_3_2", ConfigInit.CONFIG.archeryCrossbowExtraDamage);
            this.translatableText6A = Text.translatable("text.levelz.archery_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.archeryDoubleDamageChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.archery_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.archeryDoubleDamageChance * 100F));
            break;
        case "trade":
            this.translatableText2A = Text.translatable("text.levelz.trade_info_2_1", ConfigInit.CONFIG.tradeXPBonus);
            this.translatableText2B = Text.translatable("text.levelz.trade_info_2_2", ConfigInit.CONFIG.tradeXPBonus);
            this.translatableText3A = Text.translatable("text.levelz.trade_info_3_1", ConfigInit.CONFIG.tradeBonus);
            this.translatableText3B = Text.translatable("text.levelz.trade_info_3_2", ConfigInit.CONFIG.tradeBonus);
            this.translatableText6A = Text.translatable("text.levelz.trade_max_lvl_1", ConfigInit.CONFIG.tradeReputation);
            this.translatableText6B = Text.translatable("text.levelz.trade_max_lvl_2", ConfigInit.CONFIG.tradeReputation);
            break;
        case "smithing":
            this.translatableText2A = Text.translatable("text.levelz.smithing_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingToolChance * 100F));
            this.translatableText2B = Text.translatable("text.levelz.smithing_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingToolChance * 100F));
            this.translatableText3A = Text.translatable("text.levelz.smithing_info_3_1", ConfigInit.CONFIG.smithingCostBonus);
            this.translatableText3B = Text.translatable("text.levelz.smithing_info_3_2", ConfigInit.CONFIG.smithingCostBonus);
            this.translatableText6A = Text.translatable("text.levelz.smithing_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingAnvilChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.smithing_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingAnvilChance * 100F));

            this.addDrawableChild(new SkillScreen.WidgetButtonPage(this.x + 180, this.y + 7, 12, 9, 45, 80, false, true, null, button -> {
                this.client.setScreen(new SkillListScreen(this.title));
            }));
            break;
        case "mining":
            this.translatableText1A = Text.translatable("text.levelz.mining_info_1");
            this.translatableText2A = Text.translatable("text.levelz.mining_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningOreChance * 100F));
            this.translatableText2B = Text.translatable("text.levelz.mining_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningOreChance * 100F));
            this.translatableText6A = Text.translatable("text.levelz.mining_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningTntBonus * 100F));
            this.translatableText6B = Text.translatable("text.levelz.mining_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningTntBonus * 100F));

            this.addDrawableChild(new SkillScreen.WidgetButtonPage(this.x + 180, this.y + 7, 12, 9, 45, 80, false, true, null, button -> {
                this.client.setScreen(new SkillListScreen(this.title));
            }));
            break;
        case "farming":
            this.translatableText2A = Text.translatable("text.levelz.farming_info_2_1", ConfigInit.CONFIG.farmingBase);
            this.translatableText2B = Text.translatable("text.levelz.farming_info_2_2", ConfigInit.CONFIG.farmingBase);
            this.translatableText3A = Text.translatable("text.levelz.farming_info_3_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingChanceBonus * 100F));
            this.translatableText3B = Text.translatable("text.levelz.farming_info_3_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingChanceBonus * 100F));
            this.translatableText6A = Text.translatable("text.levelz.farming_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingTwinChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.farming_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingTwinChance * 100F));
            break;
        case "alchemy":
            this.translatableText1A = Text.translatable("text.levelz.alchemy_info_1");
            this.translatableText1B = Text.translatable("text.levelz.alchemy_info_1_2");
            this.translatableText2A = Text.translatable("text.levelz.alchemy_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyEnchantmentChance * 100F));
            this.translatableText2B = Text.translatable("text.levelz.alchemy_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyEnchantmentChance * 100F));
            this.translatableText6A = Text.translatable("text.levelz.alchemy_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyPotionChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.alchemy_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyPotionChance * 100F));

            this.addDrawableChild(new SkillScreen.WidgetButtonPage(this.x + 180, this.y + 7, 12, 9, 45, 80, false, true, null, button -> {
                this.client.setScreen(new SkillListScreen(this.title));
            }));
            break;
        default:
            break;
        }

        List<Text> list = new ArrayList<Text>();
        list.add(translatableText1A);
        list.add(translatableText1B);
        list.add(translatableText2A);
        list.add(translatableText2B);
        list.add(translatableText3A);
        list.add(translatableText3B);
        list.add(translatableText6A);
        list.add(translatableText6B);

        this.addDrawableChild(new SkillScrollableWidget(this.x + 10, this.y + 22, 183, 185, list, this.title, this.textRenderer));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(BACKGROUND_TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
        context.drawText(this.textRenderer, Text.translatable("text.levelz.info", Text.translatable(String.format("spritetip.levelz.%s_skill", this.title))), this.x + 6, this.y + 7, 0x3F3F3F, false);

        super.render(context, mouseX, mouseY, delta);
        DrawTabHelper.drawTab(client, context, this, x, y, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            if (ConfigInit.CONFIG.switch_screen) {
                this.client.setScreen(new SkillScreen());
            } else {
                this.close();
            }
            return true;

        } else if (KeyInit.screenKey.matchesKey(keyCode, scanCode)) {
            this.client.setScreen(new SkillScreen());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        DrawTabHelper.onTabButtonClick(client, this, this.x, this.y, mouseX, mouseY, false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public int getWidth() {
        return this.backgroundWidth;
    }

    public int getHeight() {
        return this.backgroundHeight;
    }

}
