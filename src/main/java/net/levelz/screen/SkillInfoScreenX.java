// package net.levelz.screen;

// import java.text.DecimalFormat;
// import java.util.ArrayList;

// import com.mojang.blaze3d.systems.RenderSystem;

// import org.apache.commons.lang3.StringUtils;

// import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align.Y;
// import net.fabricmc.api.EnvType;
// import net.fabricmc.api.Environment;
// import net.levelz.access.PlayerStatsManagerAccess;
// import net.levelz.data.LevelLists;
// import net.levelz.init.ConfigInit;
// import net.levelz.init.KeyInit;
// import net.levelz.stats.PlayerStatsManager;
// import net.libz.api.Tab;
// import net.minecraft.block.Blocks;
// import net.minecraft.client.gui.DrawableHelper;
// import net.minecraft.client.gui.screen.Screen;
// import net.minecraft.client.render.GameRenderer;
// import net.minecraft.client.util.math.MatrixStack;
// import net.minecraft.entity.EntityType;
// import net.minecraft.entity.player.PlayerEntity;
// import net.minecraft.item.Items;
// import net.minecraft.text.Text;
// import net.minecraft.text.Texts;
// import net.minecraft.util.Identifier;
// import net.minecraft.util.Language;
// import net.minecraft.util.math.MathHelper;
// import net.minecraft.util.registry.Registry;

// @Environment(EnvType.CLIENT)
// public class SkillInfoScreen extends Screen implements Tab {

//     public static final Identifier BACKGROUND_TEXTURE = new Identifier("levelz:textures/gui/skill_info_background.png");

//     private PlayerEntity playerEntity;
//     private PlayerStatsManager playerStatsManager;

//     private int backgroundWidth = 200;
//     private int backgroundHeight = 215;
//     private int x;
//     private int y;

//     private String title;

//     private Text translatableText1 = null;
//     private Text translatableText1B = null;
//     private Text translatableText2A = null;
//     private Text translatableText2B = null;
//     private Text translatableText3A = null;
//     private Text translatableText3B = null;
//     private Text translatableText6A = null;
//     private Text translatableText6B = null;

//     private int ySpace;
//     private boolean scrolling;
//     private int indexStartOffset;
//     private boolean this.totalYSpace != 0 = false;

//     public SkillInfoScreen(String title) {
//         super(Text.of(title));
//         this.title = title;
//     }

//     @Override
//     protected void init() {
//         super.init();
//         this.playerEntity = this.client.player;
//         this.playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager();
//         this.x = (this.width - this.backgroundWidth) / 2;
//         this.y = (this.height - this.backgroundHeight) / 2;

//         switch (this.title) {
//         case "health":
//             this.translatableText1 = Text.translatable("text.levelz.health_info_1", ConfigInit.CONFIG.healthBase);
//             this.translatableText2A = Text.translatable("text.levelz.health_info_2_1", ConfigInit.CONFIG.healthBonus);
//             this.translatableText2B = Text.translatable("text.levelz.health_info_2_2", ConfigInit.CONFIG.healthBonus);
//             this.translatableText6A = Text.translatable("text.levelz.health_max_lvl_1", ConfigInit.CONFIG.healthAbsorptionBonus);
//             this.translatableText6B = Text.translatable("text.levelz.health_max_lvl_2", ConfigInit.CONFIG.healthAbsorptionBonus);
//             break;
//         case "strength":
//             this.translatableText1 = Text.translatable("text.levelz.strength_info_1", ConfigInit.CONFIG.attackBase);
//             this.translatableText2A = Text.translatable("text.levelz.strength_info_2_1", ConfigInit.CONFIG.attackBonus);
//             this.translatableText2B = Text.translatable("text.levelz.strength_info_2_2", ConfigInit.CONFIG.attackBonus);
//             this.translatableText6A = Text.translatable("text.levelz.strength_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.strength_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F));
//             break;
//         case "agility":
//             this.translatableText1 = Text.translatable("text.levelz.agility_info_1", ConfigInit.CONFIG.movementBase);
//             this.translatableText2A = Text.translatable("text.levelz.agility_info_2_1", ConfigInit.CONFIG.movementBonus);
//             this.translatableText2B = Text.translatable("text.levelz.agility_info_2_2", ConfigInit.CONFIG.movementBonus);
//             this.translatableText3A = Text.translatable("text.levelz.agility_info_3_1", ConfigInit.CONFIG.movementFallBonus);
//             this.translatableText3B = Text.translatable("text.levelz.agility_info_3_2", ConfigInit.CONFIG.movementFallBonus);
//             this.translatableText6A = Text.translatable("text.levelz.agility_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.movementMissChance * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.agility_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.movementMissChance * 100F));
//             break;
//         case "defense":
//             this.translatableText1 = Text.translatable("text.levelz.defense_info_1", ConfigInit.CONFIG.defenseBase);
//             this.translatableText2A = Text.translatable("text.levelz.defense_info_2_1", ConfigInit.CONFIG.defenseBonus);
//             this.translatableText2B = Text.translatable("text.levelz.defense_info_2_2", ConfigInit.CONFIG.defenseBonus);
//             this.translatableText6A = Text.translatable("text.levelz.defense_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.defenseReflectChance * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.defense_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.defenseReflectChance * 100F));
//             break;
//         case "stamina":
//             this.translatableText1 = Text.translatable("text.levelz.stamina_info_1", ConfigInit.CONFIG.staminaBase);
//             this.translatableText2A = Text.translatable("text.levelz.stamina_info_2_1", ConfigInit.CONFIG.staminaBonus);
//             this.translatableText2B = Text.translatable("text.levelz.stamina_info_2_2", ConfigInit.CONFIG.staminaBonus);
//             this.translatableText3A = Text.translatable("text.levelz.stamina_info_3_1", ConfigInit.CONFIG.staminaHealthBonus);
//             this.translatableText3B = Text.translatable("text.levelz.stamina_info_3_2", ConfigInit.CONFIG.staminaHealthBonus);
//             this.translatableText6A = Text.translatable("text.levelz.stamina_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.staminaFoodBonus * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.stamina_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.staminaFoodBonus * 100F));
//             break;
//         case "luck":
//             this.translatableText1 = Text.translatable("text.levelz.luck_info_1", ConfigInit.CONFIG.luckBase);
//             this.translatableText1B = Text.translatable("text.levelz.luck_info_1_2");
//             this.translatableText2A = Text.translatable("text.levelz.luck_info_2_1", ConfigInit.CONFIG.luckBonus);
//             this.translatableText2B = Text.translatable("text.levelz.luck_info_2_2", ConfigInit.CONFIG.luckBonus);
//             this.translatableText3A = Text.translatable("text.levelz.luck_info_3_1", ConfigInit.CONFIG.luckCritBonus);
//             this.translatableText3B = Text.translatable("text.levelz.luck_info_3_2", ConfigInit.CONFIG.luckCritBonus);
//             this.translatableText6A = Text.translatable("text.levelz.luck_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.luckSurviveChance * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.luck_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.luckSurviveChance * 100F));
//             break;
//         case "archery":
//             this.translatableText2A = Text.translatable("text.levelz.archery_info_2_1", ConfigInit.CONFIG.archeryBowExtraDamage);
//             this.translatableText2B = Text.translatable("text.levelz.archery_info_2_2", ConfigInit.CONFIG.archeryBowExtraDamage);
//             this.translatableText3A = Text.translatable("text.levelz.archery_info_3_1", ConfigInit.CONFIG.archeryCrossbowExtraDamage);
//             this.translatableText3B = Text.translatable("text.levelz.archery_info_3_2", ConfigInit.CONFIG.archeryCrossbowExtraDamage);
//             this.translatableText6A = Text.translatable("text.levelz.archery_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.archeryDoubleDamageChance * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.archery_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.archeryDoubleDamageChance * 100F));
//             break;
//         case "trade":
//             this.translatableText2A = Text.translatable("text.levelz.trade_info_2_1", ConfigInit.CONFIG.tradeXPBonus);
//             this.translatableText2B = Text.translatable("text.levelz.trade_info_2_2", ConfigInit.CONFIG.tradeXPBonus);
//             this.translatableText3A = Text.translatable("text.levelz.trade_info_3_1", ConfigInit.CONFIG.tradeBonus);
//             this.translatableText3B = Text.translatable("text.levelz.trade_info_3_2", ConfigInit.CONFIG.tradeBonus);
//             this.translatableText6A = Text.translatable("text.levelz.trade_max_lvl_1", ConfigInit.CONFIG.tradeReputation);
//             this.translatableText6B = Text.translatable("text.levelz.trade_max_lvl_2", ConfigInit.CONFIG.tradeReputation);
//             break;
//         case "smithing":
//             this.translatableText2A = Text.translatable("text.levelz.smithing_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingToolChance * 100F));
//             this.translatableText2B = Text.translatable("text.levelz.smithing_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingToolChance * 100F));
//             this.translatableText3A = Text.translatable("text.levelz.smithing_info_3_1", ConfigInit.CONFIG.smithingCostBonus);
//             this.translatableText3B = Text.translatable("text.levelz.smithing_info_3_2", ConfigInit.CONFIG.smithingCostBonus);
//             this.translatableText6A = Text.translatable("text.levelz.smithing_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingAnvilChance * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.smithing_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingAnvilChance * 100F));

//             // if (!LevelLists.smithingItemList.isEmpty()) {
//             // ZWSprite smithingListIcon = new ZWSprite(name, client, 1);
//             // root.add(smithingListIcon, 180, 7, 12, 9);
//             // }

//             break;
//         case "mining":
//             this.translatableText1 = Text.translatable("text.levelz.mining_info_1");
//             this.translatableText2A = Text.translatable("text.levelz.mining_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningOreChance * 100F));
//             this.translatableText2B = Text.translatable("text.levelz.mining_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningOreChance * 100F));
//             this.translatableText6A = Text.translatable("text.levelz.mining_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningTntBonus * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.mining_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningTntBonus * 100F));

//             // if (!LevelLists.miningBlockList.isEmpty()) {
//             // ZWSprite miningListIcon = new ZWSprite(name, client, 1);
//             // root.add(miningListIcon, 180, 7, 12, 9);
//             // }

//             break;
//         case "farming":
//             this.translatableText2A = Text.translatable("text.levelz.farming_info_2_1", ConfigInit.CONFIG.farmingBase);
//             this.translatableText2B = Text.translatable("text.levelz.farming_info_2_2", ConfigInit.CONFIG.farmingBase);
//             this.translatableText3A = Text.translatable("text.levelz.farming_info_3_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingChanceBonus * 100F));
//             this.translatableText3B = Text.translatable("text.levelz.farming_info_3_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingChanceBonus * 100F));
//             this.translatableText6A = Text.translatable("text.levelz.farming_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingTwinChance * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.farming_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingTwinChance * 100F));
//             break;
//         case "alchemy":
//             this.translatableText1 = Text.translatable("text.levelz.alchemy_info_1");
//             this.translatableText2A = Text.translatable("text.levelz.alchemy_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyEnchantmentChance * 100F));
//             this.translatableText2B = Text.translatable("text.levelz.alchemy_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyEnchantmentChance * 100F));
//             this.translatableText6A = Text.translatable("text.levelz.alchemy_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyPotionChance * 100F));
//             this.translatableText6B = Text.translatable("text.levelz.alchemy_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyPotionChance * 100F));

//             // if (!LevelLists.brewingItemList.isEmpty()) {
//             // ZWSprite alchemyListIcon = new ZWSprite(name, client, 1);
//             // root.add(alchemyListIcon, 180, 7, 12, 9);
//             // }

//             break;
//         default:
//             break;
//         }
//     }

//     @Override
//     public boolean shouldPause() {
//         return false;
//     }

//     @Override
//     public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//         this.renderBackground(matrices);

//         RenderSystem.setShader(GameRenderer::getPositionTexShader);
//         RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//         RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
//         int i = (this.width - this.backgroundWidth) / 2;
//         int j = (this.height - this.backgroundHeight) / 2;
//         DrawableHelper.drawTexture(matrices, i, j, this.getZOffset(), 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);

//         this.textRenderer.draw(matrices, Text.translatable("text.levelz.info", Text.translatable(String.format("spritetip.levelz.%s_skill", this.title))), this.x + 6, this.y + 7, 0x3F3F3F);

//         ySpace = this.y + 24;
//         if (translatableTextIsNotBlank(translatableText1)) {
//             if (this.totalYSpace != 0) {
//                 this.textRenderer.draw(matrices, translatableText1, this.x + 10, ySpace, 0x3F3F3F);
//             }
//             ySpace += 14;
//         }
//         if (translatableTextIsNotBlank(translatableText1B)) {
//             if (this.totalYSpace != 0) {
//                 this.textRenderer.draw(matrices, translatableText1B, this.x + 10, ySpace, 0x3F3F3F);
//             }
//             ySpace += 14;
//         }
//         if (translatableTextIsNotBlank(translatableText2A)) {
//             if (this.totalYSpace != 0) {
//                 this.textRenderer.draw(matrices, translatableText2A, this.x + 10, ySpace, 0x3F3F3F);
//             }
//             if (translatableTextIsNotBlank(translatableText2B)) {
//                 ySpace += 10;
//                 if (this.totalYSpace != 0) {
//                     this.textRenderer.draw(matrices, translatableText2B, this.x + 10, ySpace, 0x3F3F3F);
//                 }
//             }
//             ySpace += 14;
//         }
//         if (translatableTextIsNotBlank(translatableText3A)) {
//             if (this.totalYSpace != 0) {
//                 this.textRenderer.draw(matrices, translatableText3A, this.x + 10, ySpace, 0x3F3F3F);
//             }
//             if (translatableTextIsNotBlank(translatableText3B)) {
//                 ySpace += 10;
//                 this.textRenderer.draw(matrices, translatableText3B, this.x + 10, ySpace, 0x3F3F3F);
//             }
//             ySpace += 14;
//         }

//         // Special Item
//         // 0: material, 1: skill, 2: level, 3: info, 4: boolean
//         // Other
//         // 0: skill, 1: level, 2: info, 3: boolean

//         ArrayList<Object> unlockSkillList = new ArrayList<Object>();

//         // Fill skill list
//         for (int o = 0; o < LevelLists.listOfAllLists.size(); o++) {
//             if (!LevelLists.listOfAllLists.get(o).isEmpty()) {
//                 if (LevelLists.listOfAllLists.get(o).get(0).toString().equals(this.title)) {
//                     unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(1));
//                     unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(2));
//                     unlockSkillList.add(null);
//                 } else
//                     for (int k = 0; k < LevelLists.listOfAllLists.get(o).size(); k += 5)
//                         if (LevelLists.listOfAllLists.get(o).get(k + 1).toString().equals(this.title)) {
//                             unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(2 + k));
//                             unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(3 + k));
//                             unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(0 + k));
//                         }
//             }
//         }
//         // Sort list
//         ArrayList<Object> sortedUnlockSkillList = new ArrayList<Object>();
//         // If I set j = 0 it will include 0 level unlocks!
//         for (int k = 1; k <= ConfigInit.CONFIG.maxLevel; k++) {
//             for (int o = 0; o < unlockSkillList.size(); o += 3) {
//                 if (unlockSkillList.get(o).equals(k)) {
//                     if (!sortedUnlockSkillList.contains(unlockSkillList.get(o))) {
//                         sortedUnlockSkillList.add(unlockSkillList.get(o));
//                     }
//                     sortedUnlockSkillList.add(unlockSkillList.get(o + 1));
//                     sortedUnlockSkillList.add(unlockSkillList.get(o + 2));
//                 }
//             }
//         }

//         boolean hasLvlMaxUnlock = false;
//         ySpace += 10;
//         this.textRenderer.draw(matrices, Text.translatable("text.levelz.general_info"), this.x + 10, ySpace, 0x3F3F3F);
//         // plainPanel.add(new WLabel(Text.translatable("text.levelz.general_info")), 0, gridYSpace);
//         ySpace += 16;
//         // level, object, info, object, info,..., level,...

//         for (int u = 0; u < sortedUnlockSkillList.size(); u++) {
//             if (sortedUnlockSkillList.get(u) != null && sortedUnlockSkillList.get(u).getClass() == Integer.class) {
//                 // Add level category info
//                 this.textRenderer.draw(matrices, Text.translatable("text.levelz.level", sortedUnlockSkillList.get(u)), this.x + 10, ySpace, 0x3F3F3F);
//                 // plainPanel.add(new WLabel(Text.translatable("text.levelz.level", sortedUnlockSkillList.get(u))), 0, gridYSpace);
//                 ySpace += 16;
//                 for (int g = 1; g < sortedUnlockSkillList.size() - u; g += 2) {
//                     if (sortedUnlockSkillList.get(u + g).getClass() == Integer.class) {
//                         break;
//                     }
//                     String string = sortedUnlockSkillList.get(u + g).toString();
//                     if (string.contains("minecraft:custom_"))
//                         string = sortedUnlockSkillList.get(u + g + 1).toString();

//                     Identifier identifier = new Identifier(string);
//                     boolean hit = true;

//                     if (!Registry.BLOCK.get(identifier).equals(Blocks.AIR)) {
//                         string = Registry.BLOCK.get(identifier).getTranslationKey();
//                     } else if (!Registry.ITEM.get(identifier).equals(Items.AIR)) {
//                         string = Registry.ITEM.get(identifier).getTranslationKey();
//                     } else if (!Registry.ENTITY_TYPE.get(identifier).equals(EntityType.PIG) && !EntityType.getId(EntityType.PIG).equals(identifier)) {
//                         string = Registry.ENTITY_TYPE.get(identifier).getTranslationKey();
//                     } else
//                         hit = false;

//                     Language language = Language.getInstance();

//                     if (hit)
//                         string = language.get(string);
//                     else {
//                         String translationKey = String.format("text.levelz.object_info.%s", identifier.getPath());
//                         if (language.hasTranslation(translationKey)) {
//                             string = language.get(translationKey);
//                         } else {
//                             string = StringUtils.capitalize(string.replace("minecraft:", "").replaceAll("_", " ").replace(':', ' '));
//                         }
//                     }

//                     if (sortedUnlockSkillList.get(u + g + 1) != null && !sortedUnlockSkillList.get(u + g).toString().contains("minecraft:custom_")) {
//                         String otherString = sortedUnlockSkillList.get(u + g + 1).toString();
//                         String translationKey = String.format("text.levelz.object_prefix.%s", otherString);
//                         if (language.hasTranslation(translationKey)) {
//                             otherString = language.get(translationKey);
//                         } else {
//                             otherString = otherString.replace('_', ' ');
//                         }
//                         this.textRenderer.draw(matrices, Text.translatable("text.levelz.object_info_2", StringUtils.capitalize(otherString), string), this.x + 20, ySpace, 0x3F3F3F);
//                         // plainPanel.add(new WLabel(Text.translatable("text.levelz.object_info_2", StringUtils.capitalize(otherString), string)), 10, gridYSpace);
//                     } else {
//                         this.textRenderer.draw(matrices, Text.translatable("text.levelz.object_info_1", string), this.x + 20, ySpace, 0x3F3F3F);
//                         // plainPanel.add(new WLabel(Text.translatable("text.levelz.object_info_1", string)), 10, gridYSpace);
//                     }
//                     ySpace += 16;
//                 }
//                 if (sortedUnlockSkillList.get(u).equals(ConfigInit.CONFIG.maxLevel)) {
//                     hasLvlMaxUnlock = true;
//                 } else {
//                     ySpace += 4;
//                 }
//             }

//         }

//         if (!hasLvlMaxUnlock) {
//             this.textRenderer.draw(matrices, Text.translatable("text.levelz.level", ConfigInit.CONFIG.maxLevel), this.x + 10, ySpace, 0x3F3F3F);
//             // plainPanel.add(new WLabel(Text.translatable("text.levelz.level", ConfigInit.CONFIG.maxLevel)), 0, gridYSpace);
//         } else {
//             ySpace -= 16;
//         }
//         if (translatableTextIsNotBlank(translatableText6A)) {
//             ySpace += 16;
//             this.textRenderer.draw(matrices, translatableText6A, this.x + 20, ySpace, 0x3F3F3F);
//             // plainPanel.add(new WLabel(translatableText6A), 10, gridYSpace);
//             if (translatableTextIsNotBlank(translatableText6B)) {
//                 ySpace += 10;
//                 this.textRenderer.draw(matrices, translatableText6B, this.x + 20, ySpace, 0x3F3F3F);
//                 // plainPanel.add(new WLabel(translatableText6B), 10, gridYSpace);
//             }
//         }

//         if (ySpace <= 180) {
//             // scrollPanel.setScrollingVertically(TriState.FALSE);
//         } else {
//             this.drawTexture(matrices, this.x + 186, this.y + 20, 195, 0, 8, 185);
//             this.renderScrollbar(matrices);
//         }
//         // plainPanel.setSize(200, gridYSpace);

//         // root.add(scrollPanel, 10, 20, 180, 185);
//         // root.validate(this);

//         super.render(matrices, mouseX, mouseY, delta);
//     }

//     @Override
//     public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//         if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
//             if (ConfigInit.CONFIG.switch_screen) {
//                 this.client.setScreen(new SkillScreen());
//             } else {
//                 this.close();
//             }
//             return true;

//         } else if (KeyInit.screenKey.matchesKey(keyCode, scanCode)) {
//             this.client.setScreen(new SkillScreen());
//             return true;
//         }
//         return super.keyPressed(keyCode, scanCode, modifiers);
//     }

//     private boolean canScroll() {
//         return ySpace > 180;
//     }

//     @Override
//     public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
//         // int i = ((MerchantScreenHandler) this.handler).getRecipes().size();
//         if (this.canScroll()) {
//             // int j = i - 7;
//             int j = this.ySpace / 27;
//             this.indexStartOffset = MathHelper.clamp((int) ((double) this.indexStartOffset - amount), 0, j);
//         }
//         return true;
//     }

//     @Override
//     public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
//         // int i = ((MerchantScreenHandler) this.handler).getRecipes().size();
//         int i = this.ySpace / 27;
//         if (this.scrolling) {
//             int j = this.y + 18;
//             int k = j + 182;
//             int l = i - 7;
//             float f = ((float) mouseY - (float) j - 13.5f) / ((float) (k - j) - 27.0f);
//             f = f * (float) l + 0.5f;
//             this.indexStartOffset = MathHelper.clamp((int) f, 0, l);
//             return true;
//         }
//         return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
//     }

//     @Override
//     public boolean mouseClicked(double mouseX, double mouseY, int button) {
//         this.scrolling = false;
//         int i = (this.width - this.backgroundWidth) / 2;
//         int j = (this.height - this.backgroundHeight) / 2;
//         if (this.canScroll() && mouseX > (double) (i + 94) && mouseX < (double) (i + 94 + 6) && mouseY > (double) (j + 18) && mouseY <= (double) (j + 18 + 182 + 1)) {
//             this.scrolling = true;
//         }
//         return super.mouseClicked(mouseX, mouseY, button);
//     }

//     private void renderScrollbar(MatrixStack matrices) {
//         RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
//         int i = 185 / this.ySpace;
//         if (ySpace > 180) {
//             int j = 182 - (27 + (i - 1) * 182 / i);
//             int k = 1 + j / i + 182 / i;
//             int m = Math.min(156, this.indexStartOffset * k);
//             if (this.indexStartOffset == i - 1) {
//                 m = 156;
//             }
//             this.drawTexture(matrices, this.x + 187, this.y + 21 + m, 0, 208, 6, 27);
//         } else {
//             this.drawTexture(matrices, this.x + 187, this.y + 21, 0, 214, 6, 27);
//         }
//     }

//     // private void renderScrollbar(MatrixStack matrices, int x, int y, TradeOfferList tradeOffers) {
//     // int i = tradeOffers.size() + 1 - 7;
//     // if (i > 1) {
//     // int j = 139 - (27 + (i - 1) * 139 / i);
//     // int k = 1 + j / i + 139 / i;
//     // int l = 113;
//     // int m = Math.min(113, this.indexStartOffset * k);
//     // if (this.indexStartOffset == i - 1) {
//     // m = 113;
//     // }
//     // DrawableHelper.drawTexture(matrices, x + 94, y + 18 + m, this.getZOffset(), 0.0f, 199.0f, 6, 27, 512, 256);
//     // } else {
//     // DrawableHelper.drawTexture(matrices, x + 94, y + 18, this.getZOffset(), 6.0f, 199.0f, 6, 27, 512, 256);
//     // }
//     // }

//     private boolean translatableTextIsNotBlank(Text text) {
//         if (text == null)
//             return false;
//         if (!Texts.hasTranslation(text))
//             return false;
//         return !Language.getInstance().get(text.getString()).isBlank();
//     }

// }

package net.levelz.screen;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.commons.lang3.StringUtils;

import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align.Y;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.stats.PlayerStatsManager;
import net.libz.api.Tab;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class SkillInfoScreenX extends Screen implements Tab {

    public static final Identifier BACKGROUND_TEXTURE = new Identifier("levelz:textures/gui/skill_info_background.png");

    private PlayerEntity playerEntity;
    private PlayerStatsManager playerStatsManager;

    private int backgroundWidth = 200;
    private int backgroundHeight = 215;
    private int x;
    private int y;

    private String title;

    private Text translatableText1 = null;
    private Text translatableText1B = null;
    private Text translatableText2A = null;
    private Text translatableText2B = null;
    private Text translatableText3A = null;
    private Text translatableText3B = null;
    private Text translatableText6A = null;
    private Text translatableText6B = null;

    private int totalYSpace = 0;
    private boolean scrolling;
    private int indexStartOffset;

    public SkillInfoScreenX(String title) {
        super(Text.of(title));
        this.title = title;
    }

    @Override
    protected void init() {
        super.init();

        this.playerEntity = this.client.player;
        this.playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager();
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        switch (this.title) {
        case "health":
            this.translatableText1 = Text.translatable("text.levelz.health_info_1", ConfigInit.CONFIG.healthBase);
            this.translatableText2A = Text.translatable("text.levelz.health_info_2_1", ConfigInit.CONFIG.healthBonus);
            this.translatableText2B = Text.translatable("text.levelz.health_info_2_2", ConfigInit.CONFIG.healthBonus);
            this.translatableText6A = Text.translatable("text.levelz.health_max_lvl_1", ConfigInit.CONFIG.healthAbsorptionBonus);
            this.translatableText6B = Text.translatable("text.levelz.health_max_lvl_2", ConfigInit.CONFIG.healthAbsorptionBonus);
            break;
        case "strength":
            this.translatableText1 = Text.translatable("text.levelz.strength_info_1", ConfigInit.CONFIG.attackBase);
            this.translatableText2A = Text.translatable("text.levelz.strength_info_2_1", ConfigInit.CONFIG.attackBonus);
            this.translatableText2B = Text.translatable("text.levelz.strength_info_2_2", ConfigInit.CONFIG.attackBonus);
            this.translatableText6A = Text.translatable("text.levelz.strength_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.strength_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F));
            break;
        case "agility":
            this.translatableText1 = Text.translatable("text.levelz.agility_info_1", ConfigInit.CONFIG.movementBase);
            this.translatableText2A = Text.translatable("text.levelz.agility_info_2_1", ConfigInit.CONFIG.movementBonus);
            this.translatableText2B = Text.translatable("text.levelz.agility_info_2_2", ConfigInit.CONFIG.movementBonus);
            this.translatableText3A = Text.translatable("text.levelz.agility_info_3_1", ConfigInit.CONFIG.movementFallBonus);
            this.translatableText3B = Text.translatable("text.levelz.agility_info_3_2", ConfigInit.CONFIG.movementFallBonus);
            this.translatableText6A = Text.translatable("text.levelz.agility_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.movementMissChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.agility_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.movementMissChance * 100F));
            break;
        case "defense":
            this.translatableText1 = Text.translatable("text.levelz.defense_info_1", ConfigInit.CONFIG.defenseBase);
            this.translatableText2A = Text.translatable("text.levelz.defense_info_2_1", ConfigInit.CONFIG.defenseBonus);
            this.translatableText2B = Text.translatable("text.levelz.defense_info_2_2", ConfigInit.CONFIG.defenseBonus);
            this.translatableText6A = Text.translatable("text.levelz.defense_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.defenseReflectChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.defense_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.defenseReflectChance * 100F));
            break;
        case "stamina":
            this.translatableText1 = Text.translatable("text.levelz.stamina_info_1", ConfigInit.CONFIG.staminaBase);
            this.translatableText2A = Text.translatable("text.levelz.stamina_info_2_1", ConfigInit.CONFIG.staminaBonus);
            this.translatableText2B = Text.translatable("text.levelz.stamina_info_2_2", ConfigInit.CONFIG.staminaBonus);
            this.translatableText3A = Text.translatable("text.levelz.stamina_info_3_1", ConfigInit.CONFIG.staminaHealthBonus);
            this.translatableText3B = Text.translatable("text.levelz.stamina_info_3_2", ConfigInit.CONFIG.staminaHealthBonus);
            this.translatableText6A = Text.translatable("text.levelz.stamina_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.staminaFoodBonus * 100F));
            this.translatableText6B = Text.translatable("text.levelz.stamina_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.staminaFoodBonus * 100F));
            break;
        case "luck":
            this.translatableText1 = Text.translatable("text.levelz.luck_info_1", ConfigInit.CONFIG.luckBase);
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

            // if (!LevelLists.smithingItemList.isEmpty()) {
            // ZWSprite smithingListIcon = new ZWSprite(name, client, 1);
            // root.add(smithingListIcon, 180, 7, 12, 9);
            // }

            break;
        case "mining":
            this.translatableText1 = Text.translatable("text.levelz.mining_info_1");
            this.translatableText2A = Text.translatable("text.levelz.mining_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningOreChance * 100F));
            this.translatableText2B = Text.translatable("text.levelz.mining_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningOreChance * 100F));
            this.translatableText6A = Text.translatable("text.levelz.mining_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningTntBonus * 100F));
            this.translatableText6B = Text.translatable("text.levelz.mining_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningTntBonus * 100F));

            // if (!LevelLists.miningBlockList.isEmpty()) {
            // ZWSprite miningListIcon = new ZWSprite(name, client, 1);
            // root.add(miningListIcon, 180, 7, 12, 9);
            // }

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
            this.translatableText1 = Text.translatable("text.levelz.alchemy_info_1");
            this.translatableText2A = Text.translatable("text.levelz.alchemy_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyEnchantmentChance * 100F));
            this.translatableText2B = Text.translatable("text.levelz.alchemy_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyEnchantmentChance * 100F));
            this.translatableText6A = Text.translatable("text.levelz.alchemy_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyPotionChance * 100F));
            this.translatableText6B = Text.translatable("text.levelz.alchemy_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyPotionChance * 100F));

            // if (!LevelLists.brewingItemList.isEmpty()) {
            // ZWSprite alchemyListIcon = new ZWSprite(name, client, 1);
            // root.add(alchemyListIcon, 180, 7, 12, 9);
            // }

            break;
        default:
            break;
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        DrawableHelper.drawTexture(matrices, i, j, this.getZOffset(), 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);

        this.textRenderer.draw(matrices, Text.translatable("text.levelz.info", Text.translatable(String.format("spritetip.levelz.%s_skill", this.title))), this.x + 6, this.y + 7, 0x3F3F3F);

        int ySpace = this.y + 24;
        // System.out.println(this.indexStartOffset + " : " + this.totalYSpace + " : " + (this.totalYSpace / 27));
        if (translatableTextIsNotBlank(translatableText1)) {
            if (this.totalYSpace != 0) {
                this.textRenderer.draw(matrices, translatableText1, this.x + 10, ySpace, 0x3F3F3F);
            }
            ySpace += 14;
        }
        if (translatableTextIsNotBlank(translatableText1B)) {
            if (this.totalYSpace != 0) {
                this.textRenderer.draw(matrices, translatableText1B, this.x + 10, ySpace, 0x3F3F3F);
            }
            ySpace += 14;
        }
        if (translatableTextIsNotBlank(translatableText2A)) {
            if (this.totalYSpace != 0) {
                this.textRenderer.draw(matrices, translatableText2A, this.x + 10, ySpace, 0x3F3F3F);
            }
            if (translatableTextIsNotBlank(translatableText2B)) {
                ySpace += 10;
                if (this.totalYSpace != 0) {
                    this.textRenderer.draw(matrices, translatableText2B, this.x + 10, ySpace, 0x3F3F3F);
                }
            }
            ySpace += 14;
        }
        if (translatableTextIsNotBlank(translatableText3A)) {
            if (this.totalYSpace != 0) {
                this.textRenderer.draw(matrices, translatableText3A, this.x + 10, ySpace, 0x3F3F3F);
            }
            if (translatableTextIsNotBlank(translatableText3B)) {
                ySpace += 10;
                if (this.totalYSpace != 0) {
                    this.textRenderer.draw(matrices, translatableText3B, this.x + 10, ySpace, 0x3F3F3F);
                }
            }
            ySpace += 14;
        }

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
        ArrayList<Object> sortedUnlockSkillList = new ArrayList<Object>();
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

        boolean hasLvlMaxUnlock = false;
        ySpace += 10;
        if (this.totalYSpace != 0) {
            this.textRenderer.draw(matrices, Text.translatable("text.levelz.general_info"), this.x + 10, ySpace, 0x3F3F3F);
        }
        // plainPanel.add(new WLabel(Text.translatable("text.levelz.general_info")), 0, gridYSpace);
        ySpace += 16;
        // level, object, info, object, info,..., level,...

        for (int u = 0; u < sortedUnlockSkillList.size(); u++) {
            if (sortedUnlockSkillList.get(u) != null && sortedUnlockSkillList.get(u).getClass() == Integer.class) {
                // Add level category info
                if (this.totalYSpace != 0) {
                    this.textRenderer.draw(matrices, Text.translatable("text.levelz.level", sortedUnlockSkillList.get(u)), this.x + 10, ySpace, 0x3F3F3F);
                }
                // plainPanel.add(new WLabel(Text.translatable("text.levelz.level", sortedUnlockSkillList.get(u))), 0, gridYSpace);
                ySpace += 16;
                for (int g = 1; g < sortedUnlockSkillList.size() - u; g += 2) {
                    if (sortedUnlockSkillList.get(u + g).getClass() == Integer.class) {
                        break;
                    }
                    String string = sortedUnlockSkillList.get(u + g).toString();
                    if (string.contains("minecraft:custom_"))
                        string = sortedUnlockSkillList.get(u + g + 1).toString();

                    Identifier identifier = new Identifier(string);
                    boolean hit = true;

                    if (!Registry.BLOCK.get(identifier).equals(Blocks.AIR)) {
                        string = Registry.BLOCK.get(identifier).getTranslationKey();
                    } else if (!Registry.ITEM.get(identifier).equals(Items.AIR)) {
                        string = Registry.ITEM.get(identifier).getTranslationKey();
                    } else if (!Registry.ENTITY_TYPE.get(identifier).equals(EntityType.PIG) && !EntityType.getId(EntityType.PIG).equals(identifier)) {
                        string = Registry.ENTITY_TYPE.get(identifier).getTranslationKey();
                    } else
                        hit = false;

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
                        if (this.totalYSpace != 0) {
                            this.textRenderer.draw(matrices, Text.translatable("text.levelz.object_info_2", StringUtils.capitalize(otherString), string), this.x + 20, ySpace, 0x3F3F3F);
                        }
                        // plainPanel.add(new WLabel(Text.translatable("text.levelz.object_info_2", StringUtils.capitalize(otherString), string)), 10, gridYSpace);
                    } else {
                        if (this.totalYSpace != 0) {
                            this.textRenderer.draw(matrices, Text.translatable("text.levelz.object_info_1", string), this.x + 20, ySpace, 0x3F3F3F);
                        }
                        // plainPanel.add(new WLabel(Text.translatable("text.levelz.object_info_1", string)), 10, gridYSpace);
                    }
                    ySpace += 16;
                }
                if (sortedUnlockSkillList.get(u).equals(ConfigInit.CONFIG.maxLevel)) {
                    hasLvlMaxUnlock = true;
                } else {
                    ySpace += 4;
                }
            }

        }

        if (!hasLvlMaxUnlock) {
            if (this.totalYSpace != 0) {
                this.textRenderer.draw(matrices, Text.translatable("text.levelz.level", ConfigInit.CONFIG.maxLevel), this.x + 10, ySpace, 0x3F3F3F);
            }
            // plainPanel.add(new WLabel(Text.translatable("text.levelz.level", ConfigInit.CONFIG.maxLevel)), 0, gridYSpace);
        } else {
            ySpace -= 16;
        }
        if (translatableTextIsNotBlank(translatableText6A)) {
            ySpace += 16;
            if (this.totalYSpace != 0) {
                this.textRenderer.draw(matrices, translatableText6A, this.x + 20, ySpace, 0x3F3F3F);
            }
            // plainPanel.add(new WLabel(translatableText6A), 10, gridYSpace);
            if (translatableTextIsNotBlank(translatableText6B)) {
                ySpace += 10;
                if (this.totalYSpace != 0) {
                    this.textRenderer.draw(matrices, translatableText6B, this.x + 20, ySpace, 0x3F3F3F);
                }
                // plainPanel.add(new WLabel(translatableText6B), 10, gridYSpace);
            }
        }

        // if (ySpace <= 180) {
        // scrollPanel.setScrollingVertically(TriState.FALSE);
        // } else {
        if (this.totalYSpace == 0) {
            this.totalYSpace = ySpace - this.y;
        }
        if (this.totalYSpace > 185) {
            this.drawTexture(matrices, this.x + 186, this.y + 20, 195, 0, 8, 185);
            this.renderScrollbar(matrices);
        }
        // }

        // plainPanel.setSize(200, gridYSpace);

        // root.add(scrollPanel, 10, 20, 180, 185);
        // root.validate(this);

        super.render(matrices, mouseX, mouseY, delta);
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

    private boolean canScroll() {
        return this.totalYSpace > 185;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // int i = ((MerchantScreenHandler) this.handler).getRecipes().size();
        if (this.canScroll()) {
            // int j = i - 7;
            int j = this.totalYSpace / 27;
            this.indexStartOffset = MathHelper.clamp((int) ((double) this.indexStartOffset - amount), 0, j);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // int i = ((MerchantScreenHandler) this.handler).getRecipes().size();
        int i = this.totalYSpace / 27;
        if (this.scrolling) {
            int j = this.y + 18;
            int k = j + 182;
            int l = i - 7;
            float f = ((float) mouseY - (float) j - 13.5f) / ((float) (k - j) - 27.0f);
            f = f * (float) l + 0.5f;
            this.indexStartOffset = MathHelper.clamp((int) f, 0, l);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        if (this.canScroll() && mouseX > (double) (i + 94) && mouseX < (double) (i + 94 + 6) && mouseY > (double) (j + 18) && mouseY <= (double) (j + 18 + 182 + 1)) {
            this.scrolling = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderScrollbar(MatrixStack matrices) {
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int i = this.totalYSpace - 185 / 27;
        if (this.totalYSpace > 185) {
            int j = 182 - (27 + (i - 1) * 182 / i);
            int k = 1 + j / i + 182 / i;
            int m = Math.min(156, this.indexStartOffset * k);
            if (this.indexStartOffset == i - 1) {
                m = 156;
            }
            this.drawTexture(matrices, this.x + 187, this.y + 21 + m, 0, 208, 6, 27);
        } else {
            this.drawTexture(matrices, this.x + 187, this.y + 21, 0, 214, 6, 27);
        }
    }

    // private void renderScrollbar(MatrixStack matrices, int x, int y, TradeOfferList tradeOffers) {
    // int i = tradeOffers.size() + 1 - 7;
    // if (i > 1) {
    // int j = 139 - (27 + (i - 1) * 139 / i);
    // int k = 1 + j / i + 139 / i;
    // int l = 113;
    // int m = Math.min(113, this.indexStartOffset * k);
    // if (this.indexStartOffset == i - 1) {
    // m = 113;
    // }
    // DrawableHelper.drawTexture(matrices, x + 94, y + 18 + m, this.getZOffset(), 0.0f, 199.0f, 6, 27, 512, 256);
    // } else {
    // DrawableHelper.drawTexture(matrices, x + 94, y + 18, this.getZOffset(), 6.0f, 199.0f, 6, 27, 512, 256);
    // }
    // }

    private boolean translatableTextIsNotBlank(Text text) {
        if (text == null)
            return false;
        if (!Texts.hasTranslation(text))
            return false;
        return !Language.getInstance().get(text.getString()).isBlank();
    }

}
