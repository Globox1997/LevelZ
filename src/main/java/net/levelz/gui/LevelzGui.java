package net.levelz.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.network.PlayerStatsClientPacket;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class LevelzGui extends LightweightGuiDescription {

    public static final Identifier GUI_ICONS = new Identifier("levelz:textures/gui/icons.png");

    public LevelzGui(MinecraftClient client) {
        PlayerEntity playerEntity = client.player;

        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(200, 200);
        // Top label
        WLabel topLlabel = new WLabel(new TranslatableText("text.levelz.gui.title", playerEntity.getName().getString()));//new LiteralText(playerEntity.getName().getString() + " Skills"), 0xFFFFFF);
        root.add(topLlabel, 80, 7);
        // Small icons
        WSprite lifeIcon = new WSprite(GUI_ICONS, 0F, 0F, 1F / 28.2865F, 1F / 28.2865F);
        WSprite protectionIcon = new WSprite(GUI_ICONS, 1F / 28.2865F, 0F, 2F / 28.305F, 1F / 28.2865F);
        WSprite speedIcon = new WSprite(GUI_ICONS, 2F / 28.2865F, 0F, 3F / 28.355F, 1F / 28.2865F);
        WSprite damageIcon = new WSprite(GUI_ICONS, 3F / 28.2865F, 0F, 4F / 28.4F, 1F / 28.2865F);
        WSprite foodIcon = new WSprite(GUI_ICONS, 4F / 28.2865F, 0F, 5F / 28.4F, 1F / 28.2865F);
        WSprite fortuneIcon = new WSprite(GUI_ICONS, 5F / 28.2865F, 0F, 6F / 28.2865F, 1F / 28.2865F);

        root.add(lifeIcon, 58, 21, 10, 10);
        root.add(protectionIcon, 58, 34, 10, 10);
        root.add(damageIcon, 108, 21, 10, 10);
        root.add(speedIcon, 108, 34, 10, 10);
        root.add(foodIcon, 155, 21, 10, 10);
        root.add(fortuneIcon, 155, 34, 10, 10);

        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager(playerEntity);
        // Small icon labels
        WDynamicLabel lifeLabel = new WDynamicLabel(() -> "" + Math.round(playerEntity.getHealth()));
        WDynamicLabel protectionLabel = new WDynamicLabel(
                () -> "" + BigDecimal.valueOf(playerEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR)).setScale(2, RoundingMode.HALF_DOWN).floatValue());
        WDynamicLabel damageLabel = new WDynamicLabel(() -> "" + getDamageLabel(playerStatsManager, client.player));
        WDynamicLabel speedLabel = new WDynamicLabel(
                () -> "" + BigDecimal.valueOf(playerEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 10D).setScale(2, RoundingMode.HALF_DOWN).floatValue());
        WDynamicLabel foodLabel = new WDynamicLabel(() -> "" + Math.round(playerEntity.getHungerManager().getFoodLevel()));
        WDynamicLabel fortuneLabel = new WDynamicLabel(() -> "" + BigDecimal.valueOf(playerEntity.getAttributeValue(EntityAttributes.GENERIC_LUCK)).setScale(2, RoundingMode.HALF_DOWN).floatValue());
        WDynamicLabel overallLevel = new WDynamicLabel(() -> String.format(Language.getInstance().get("text.levelz.gui.level"), playerStatsManager.getLevel("level")));
        WDynamicLabel skillPoints = new WDynamicLabel(() -> String.format(Language.getInstance().get("text.levelz.gui.points"), playerStatsManager.getLevel("points")));
        WDynamicLabel nextLevel = new WDynamicLabel(
                () -> "XP " + (int) (playerStatsManager.levelProgress * playerStatsManager.getNextLevelExperience()) + " / " + playerStatsManager.getNextLevelExperience());

        root.add(lifeLabel, 74, 22);
        root.add(protectionLabel, 74, 36);
        root.add(damageLabel, 124, 22);
        root.add(speedLabel, 124, 36);
        root.add(foodLabel, 171, 22);
        root.add(fortuneLabel, 171, 36);
        root.add(overallLevel, 72, 51);
        root.add(skillPoints, 130, 51);
        root.add(nextLevel, 95, 70);
        // Skill labels
        WDynamicLabel healthLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("health") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel strengthLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("strength") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel agilityLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("agility") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel defenseLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("defense") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel staminaLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("stamina") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel luckLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("luck") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel archeryLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("archery") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel tradeLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("trade") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel smithingLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("smithing") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel miningLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("mining") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel farmingLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("farming") + "/" + ConfigInit.CONFIG.maxLevel);
        WDynamicLabel alchemyLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("alchemy") + "/" + ConfigInit.CONFIG.maxLevel);

        root.add(healthLabel, 39, 95);
        root.add(strengthLabel, 39, 115);
        root.add(agilityLabel, 39, 135);
        root.add(defenseLabel, 39, 155);
        root.add(staminaLabel, 39, 175);
        root.add(luckLabel, 39, 195);
        root.add(archeryLabel, 129, 95);
        root.add(tradeLabel, 129, 115);
        root.add(smithingLabel, 129, 135);
        root.add(miningLabel, 129, 155);
        root.add(farmingLabel, 129, 175);
        root.add(alchemyLabel, 129, 195);

        // Skill sprites
        ZWSprite healthIcon = new ZWSprite("health", GUI_ICONS, client, 0F, 1F / 16F, 1F / 16F, 2F / 16F);
        ZWSprite strengthIcon = new ZWSprite("strength", GUI_ICONS, client, 1F / 16F, 1F / 16F, 2F / 16F, 2F / 16F);
        ZWSprite agilityIcon = new ZWSprite("agility", GUI_ICONS, client, 2F / 16F, 1F / 16F, 3F / 16F, 2F / 16F);
        ZWSprite defenseIcon = new ZWSprite("defense", GUI_ICONS, client, 3F / 16F, 1F / 16F, 4F / 16F, 2F / 16F);
        ZWSprite staminaIcon = new ZWSprite("stamina", GUI_ICONS, client, 4F / 16F, 1F / 16F, 5F / 16F, 2F / 16F);
        ZWSprite luckIcon = new ZWSprite("luck", GUI_ICONS, client, 5F / 16F, 1F / 16F, 6F / 16F, 2F / 16F);
        ZWSprite archeryIcon = new ZWSprite("archery", GUI_ICONS, client, 6F / 16F, 1F / 16F, 7F / 16F, 2F / 16F);
        ZWSprite tradeIcon = new ZWSprite("trade", GUI_ICONS, client, 7F / 16F, 1F / 16F, 8F / 16F, 2F / 16F);
        ZWSprite smithingIcon = new ZWSprite("smithing", GUI_ICONS, client, 8F / 16F, 1F / 16F, 9F / 16F, 2F / 16F);
        ZWSprite miningIcon = new ZWSprite("mining", GUI_ICONS, client, 9F / 16F, 1F / 16F, 10F / 16F, 2F / 16F);
        ZWSprite farmingIcon = new ZWSprite("farming", GUI_ICONS, client, 10F / 16F, 1F / 16F, 11F / 16F, 2F / 16F);
        ZWSprite alchemyIcon = new ZWSprite("alchemy", GUI_ICONS, client, 11F / 16F, 1F / 16F, 12F / 16F, 2F / 16F);

        // Skill sprite tooltip
        healthIcon.addText(new TranslatableText("spritetip.levelz.health_skill").getString());
        healthIcon.addText(new TranslatableText("spritetip.levelz.health_skill_info").getString());

        strengthIcon.addText(new TranslatableText("spritetip.levelz.strength_skill").getString());
        strengthIcon.addText(new TranslatableText("spritetip.levelz.strength_skill_info_1").getString());
        strengthIcon.addText(new TranslatableText("spritetip.levelz.strength_skill_info_2").getString());

        agilityIcon.addText(new TranslatableText("spritetip.levelz.agility_skill").getString());
        agilityIcon.addText(new TranslatableText("spritetip.levelz.agility_skill_info_1").getString());
        agilityIcon.addText(new TranslatableText("spritetip.levelz.agility_skill_info_2").getString());

        defenseIcon.addText(new TranslatableText("spritetip.levelz.defense_skill").getString());
        defenseIcon.addText(new TranslatableText("spritetip.levelz.defense_skill_info_1").getString());
        defenseIcon.addText(new TranslatableText("spritetip.levelz.defense_skill_info_2").getString());

        staminaIcon.addText(new TranslatableText("spritetip.levelz.stamina_skill").getString());
        staminaIcon.addText(new TranslatableText("spritetip.levelz.stamina_skill_info_1").getString());
        staminaIcon.addText(new TranslatableText("spritetip.levelz.stamina_skill_info_2").getString());

        luckIcon.addText(new TranslatableText("spritetip.levelz.luck_skill").getString());
        luckIcon.addText(new TranslatableText("spritetip.levelz.luck_skill_info_1").getString());
        luckIcon.addText(new TranslatableText("spritetip.levelz.luck_skill_info_2").getString());

        archeryIcon.addText(new TranslatableText("spritetip.levelz.archery_skill").getString());
        archeryIcon.addText(new TranslatableText("spritetip.levelz.archery_skill_info_1").getString());
        archeryIcon.addText(new TranslatableText("spritetip.levelz.archery_skill_info_2").getString());
        archeryIcon.addText(new TranslatableText("spritetip.levelz.archery_skill_info_3").getString());

        tradeIcon.addText(new TranslatableText("spritetip.levelz.trade_skill").getString());
        tradeIcon.addText(new TranslatableText("spritetip.levelz.trade_skill_info").getString());

        smithingIcon.addText(new TranslatableText("spritetip.levelz.smithing_skill").getString());
        smithingIcon.addText(new TranslatableText("spritetip.levelz.smithing_skill_info_1").getString());
        smithingIcon.addText(new TranslatableText("spritetip.levelz.smithing_skill_info_2").getString());

        miningIcon.addText(new TranslatableText("spritetip.levelz.mining_skill").getString());
        miningIcon.addText(new TranslatableText("spritetip.levelz.mining_skill_info_1").getString());
        miningIcon.addText(new TranslatableText("spritetip.levelz.mining_skill_info_2").getString());

        farmingIcon.addText(new TranslatableText("spritetip.levelz.farming_skill").getString());
        farmingIcon.addText(new TranslatableText("spritetip.levelz.farming_skill_info").getString());

        alchemyIcon.addText(new TranslatableText("spritetip.levelz.alchemy_skill").getString());
        alchemyIcon.addText(new TranslatableText("spritetip.levelz.alchemy_skill_info").getString());

        root.add(healthIcon, 15, 90, 16, 16);
        root.add(strengthIcon, 15, 110, 16, 16);
        root.add(agilityIcon, 15, 130, 16, 16);
        root.add(defenseIcon, 15, 150, 16, 16);
        root.add(staminaIcon, 15, 170, 16, 16);
        root.add(luckIcon, 15, 190, 16, 16);
        root.add(archeryIcon, 105, 90, 16, 16);
        root.add(tradeIcon, 105, 110, 16, 16);
        root.add(smithingIcon, 105, 130, 16, 16);
        root.add(miningIcon, 105, 150, 16, 16);
        root.add(farmingIcon, 105, 170, 16, 16);
        root.add(alchemyIcon, 105, 190, 16, 16);

        // Info button
        ZWSprite infoIcon = new ZWSprite("info", null, 1);
        infoIcon.addText(new TranslatableText("text.levelz.more_info").getString());
        root.add(infoIcon, 178, 73, 11, 13);

        // Skill buttons
        ZWButton healthButton = new ZWButton();
        ZWButton strengthButton = new ZWButton();
        ZWButton agilityButton = new ZWButton();
        ZWButton defenseButton = new ZWButton();
        ZWButton staminaButton = new ZWButton();
        ZWButton luckButton = new ZWButton();
        ZWButton archeryButton = new ZWButton();
        ZWButton tradeButton = new ZWButton();
        ZWButton smithingButton = new ZWButton();
        ZWButton miningButton = new ZWButton();
        ZWButton farmingButton = new ZWButton();
        ZWButton alchemyButton = new ZWButton();

        root.add(healthButton, 77, 91);
        root.add(strengthButton, 77, 111);
        root.add(agilityButton, 77, 131);
        root.add(defenseButton, 77, 151);
        root.add(staminaButton, 77, 171);
        root.add(luckButton, 77, 191);
        root.add(archeryButton, 167, 91);
        root.add(tradeButton, 167, 111);
        root.add(smithingButton, 167, 131);
        root.add(miningButton, 167, 151);
        root.add(farmingButton, 167, 171);
        root.add(alchemyButton, 167, 191);
        // Button mechanic
        setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton, alchemyButton,
                playerStatsManager);

        healthButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "health");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                    .setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) + ConfigInit.CONFIG.healthBonus);
        });
        strengthButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "strength");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                    .setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + ConfigInit.CONFIG.attackBonus);
        });
        agilityButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "agility");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + ConfigInit.CONFIG.movementBonus);
        });
        defenseButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "defense");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR) + ConfigInit.CONFIG.defenseBonus);
        });
        staminaButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "stamina");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
        });
        luckButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "luck");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_LUCK) + ConfigInit.CONFIG.luckBonus);
        });
        archeryButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "archery");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
        });
        tradeButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "trade");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
        });
        smithingButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "smithing");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
        });
        miningButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "mining");
            PlayerStatsServerPacket.syncLockedBlockList(playerStatsManager);
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
        });
        farmingButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "farming");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
        });
        alchemyButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "alchemy");
            PlayerStatsServerPacket.syncLockedBrewingItemList(playerStatsManager);
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    alchemyButton, playerStatsManager);
        });

        root.validate(this);
    }

    private void setButtonEnabled(WButton wButton1, WButton wButton2, WButton wButton3, WButton wButton4, WButton wButton5, WButton wButton6, WButton wButton7, WButton wButton8, WButton wButton9,
            WButton wButton10, WButton wButton11, WButton wButton12, PlayerStatsManager playerStatsManager) {
        Boolean enoughPoints = playerStatsManager.getLevel("points") > 0;
        wButton1.setEnabled(enoughPoints && playerStatsManager.getLevel("health") < ConfigInit.CONFIG.maxLevel);
        wButton2.setEnabled(enoughPoints && playerStatsManager.getLevel("strength") < ConfigInit.CONFIG.maxLevel);
        wButton3.setEnabled(enoughPoints && playerStatsManager.getLevel("agility") < ConfigInit.CONFIG.maxLevel);
        wButton4.setEnabled(enoughPoints && playerStatsManager.getLevel("defense") < ConfigInit.CONFIG.maxLevel);
        wButton5.setEnabled(enoughPoints && playerStatsManager.getLevel("stamina") < ConfigInit.CONFIG.maxLevel);
        wButton6.setEnabled(enoughPoints && playerStatsManager.getLevel("luck") < ConfigInit.CONFIG.maxLevel);
        wButton7.setEnabled(enoughPoints && playerStatsManager.getLevel("archery") < ConfigInit.CONFIG.maxLevel);
        wButton8.setEnabled(enoughPoints && playerStatsManager.getLevel("trade") < ConfigInit.CONFIG.maxLevel);
        wButton9.setEnabled(enoughPoints && playerStatsManager.getLevel("smithing") < ConfigInit.CONFIG.maxLevel);
        wButton10.setEnabled(enoughPoints && playerStatsManager.getLevel("mining") < ConfigInit.CONFIG.maxLevel);
        wButton11.setEnabled(enoughPoints && playerStatsManager.getLevel("farming") < ConfigInit.CONFIG.maxLevel);
        wButton12.setEnabled(enoughPoints && playerStatsManager.getLevel("alchemy") < ConfigInit.CONFIG.maxLevel);
    }

    private String getDamageLabel(PlayerStatsManager playerStatsManager, PlayerEntity playerEntity) {
        float damage = 0.0F;
        boolean isSword = false;
        ItemStack itemStack = playerEntity.getMainHandStack();
        if (itemStack.getItem() instanceof ToolItem) {
            ArrayList<Object> levelList = new ArrayList<Object>();
            if (itemStack.isIn(FabricToolTags.SWORDS)) {
                levelList = LevelLists.swordList;
                isSword = true;
            } else if (itemStack.isIn(FabricToolTags.AXES))
                levelList = LevelLists.axeList;
            else if (itemStack.isIn(FabricToolTags.HOES))
                levelList = LevelLists.hoeList;
            else
                levelList = LevelLists.toolList;
            if (PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((ToolItem) itemStack.getItem()).getMaterial().toString().toLowerCase(), false)) {
                if (isSword)
                    damage = ((SwordItem) itemStack.getItem()).getAttackDamage();
                else if (itemStack.getItem() instanceof MiningToolItem)
                    damage = ((MiningToolItem) itemStack.getItem()).getAttackDamage();
            }

        }
        damage += playerEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        return "" + BigDecimal.valueOf(damage).setScale(2, RoundingMode.HALF_DOWN).floatValue();
    }

}