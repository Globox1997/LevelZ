package net.levelz.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.JsonReaderInit;
import net.levelz.network.PlayerStatsClientPacket;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LevelzGui extends LightweightGuiDescription {
    public LevelzGui(PlayerEntity playerEntity) {

        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(200, 200);
        // Top label
        WLabel topLlabel = new WLabel(new LiteralText(playerEntity.getName().getString() + " Skills"), 0xFFFFFF);
        root.add(topLlabel, 80, ConfigInit.CONFIG.test1);
        // Small icons
        WSprite lifeIcon = new WSprite(new Identifier("levelz:textures/gui/icons.png"), 0F, 0F, 1F / 28.2865F, 1F / 28.2865F);
        WSprite protectionIcon = new WSprite(new Identifier("levelz:textures/gui/icons.png"), 1F / 28.2865F, 0F, 2F / 28.305F, 1F / 28.2865F);
        WSprite speedIcon = new WSprite(new Identifier("levelz:textures/gui/icons.png"), 2F / 28.2865F, 0F, 3F / 28.355F, 1F / 28.2865F);
        WSprite damageIcon = new WSprite(new Identifier("levelz:textures/gui/icons.png"), 3F / 28.2865F, 0F, 4F / 28.4F, 1F / 28.2865F);
        WSprite foodIcon = new WSprite(new Identifier("levelz:textures/gui/icons.png"), 4F / 28.2865F, 0F, 5F / 28.4F, 1F / 28.2865F);
        WSprite fortuneIcon = new WSprite(new Identifier("levelz:textures/gui/icons.png"), 5F / 28.2865F, 0F, 6F / 28.2865F, 1F / 28.2865F);

        root.add(lifeIcon, 58, 15, 10, 10);
        root.add(protectionIcon, 58, 28, 10, 10);
        root.add(damageIcon, 108, 15, 10, 10);
        root.add(speedIcon, 108, 28, 10, 10);
        root.add(foodIcon, 155, 15, 10, 10);
        root.add(fortuneIcon, 155, 28, 10, 10);

        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager(playerEntity);
        // Small icon labels
        WDynamicLabel lifeLabel = new WDynamicLabel(() -> "" + playerEntity.getMaxHealth());
        WDynamicLabel protectionLabel = new WDynamicLabel(() -> "" + playerEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR));
        WDynamicLabel damageLabel = new WDynamicLabel(() -> "" + (playerEntity.getMainHandStack().getItem() instanceof SwordItem
                && ((SwordItem) playerEntity.getMainHandStack().getItem()).getMaterial().getMiningLevel() * 4 <= playerStatsManager.getLevel("strength")
                        ? ((SwordItem) playerEntity.getMainHandStack().getItem()).getAttackDamage()
                        : 0 + playerEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)));
        WDynamicLabel speedLabel = new WDynamicLabel(
                () -> "" + BigDecimal.valueOf(playerEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setScale(3, RoundingMode.HALF_DOWN).floatValue());
        WDynamicLabel foodLabel = new WDynamicLabel(() -> "" + playerEntity.getHungerManager().getFoodLevel());
        WDynamicLabel fortuneLabel = new WDynamicLabel(() -> "" + playerEntity.getAttributeValue(EntityAttributes.GENERIC_LUCK));
        WDynamicLabel overallLevel = new WDynamicLabel(() -> "Level " + playerStatsManager.getLevel("level"));
        WDynamicLabel skillPoints = new WDynamicLabel(() -> "Points " + playerStatsManager.getLevel("points"));
        WDynamicLabel nextLevel = new WDynamicLabel(
                () -> "XP " + (int) (playerStatsManager.levelProgress * playerStatsManager.getNextLevelExperience()) + " / " + playerStatsManager.getNextLevelExperience());

        root.add(lifeLabel, 74, 16);
        root.add(protectionLabel, 74, 30);
        root.add(damageLabel, 124, 16);
        root.add(speedLabel, 124, 30);
        root.add(foodLabel, 171, 16);
        root.add(fortuneLabel, 171, 30);
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
        WDynamicLabel buildingLabel = new WDynamicLabel(() -> "" + playerStatsManager.getLevel("building") + "/" + ConfigInit.CONFIG.maxLevel);

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
        root.add(buildingLabel, 129, 195);
        // Skill sprites
        ZWSprite healthIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 0F, 1F / 16F, 1F / 16F, 2F / 16F);
        ZWSprite strengthIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 1F / 16F, 1F / 16F, 2F / 16F, 2F / 16F);
        ZWSprite agilityIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 2F / 16F, 1F / 16F, 3F / 16F, 2F / 16F);
        ZWSprite defenseIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 3F / 16F, 1F / 16F, 4F / 16F, 2F / 16F);
        ZWSprite staminaIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 4F / 16F, 1F / 16F, 5F / 16F, 2F / 16F);
        ZWSprite luckIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 5F / 16F, 1F / 16F, 6F / 16F, 2F / 16F);
        ZWSprite archeryIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 6F / 16F, 1F / 16F, 7F / 16F, 2F / 16F);
        ZWSprite tradeIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 7F / 16F, 1F / 16F, 8F / 16F, 2F / 16F);
        ZWSprite smithingIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 8F / 16F, 1F / 16F, 9F / 16F, 2F / 16F);
        ZWSprite miningIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 9F / 16F, 1F / 16F, 10F / 16F, 2F / 16F);
        ZWSprite farmingIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 10F / 16F, 1F / 16F, 11F / 16F, 2F / 16F);
        ZWSprite buildingIcon = new ZWSprite(new Identifier("levelz:textures/gui/icons.png"), 11F / 16F, 1F / 16F, 12F / 16F, 2F / 16F);
        // Skill sprite tooltip
        healthIcon.addText("Increases Health");
        strengthIcon.addText("Increases Melee Damage");
        agilityIcon.addText("Increases Movement Speed");
        agilityIcon.addText("Decreases Fall Damage");
        defenseIcon.addText("Increases Protection");
        staminaIcon.addText("Increases Food Saturation");
        luckIcon.addText("Increases Loot Value");
        archeryIcon.addText("Increases Range Damage");
        tradeIcon.addText("Decreases Trade Price");
        smithingIcon.addText("Decreases Smithing XP Price");
        miningIcon.addText("Unlocks Mining Sources");
        miningIcon.addText("Increases Mining Speed");
        farmingIcon.addText("Unlocks Farming Sources");
        buildingIcon.addText("Unlocks Building Sources");

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
        root.add(buildingIcon, 105, 190, 16, 16);
        // Skill buttons
        WButton healthButton = new WButton(new LiteralText("+"));
        WButton strengthButton = new WButton(new LiteralText("+"));
        WButton agilityButton = new WButton(new LiteralText("+"));
        WButton defenseButton = new WButton(new LiteralText("+"));
        WButton staminaButton = new WButton(new LiteralText("+"));
        WButton luckButton = new WButton(new LiteralText("+"));
        WButton archeryButton = new WButton(new LiteralText("+"));
        WButton tradeButton = new WButton(new LiteralText("+"));
        WButton smithingButton = new WButton(new LiteralText("+"));
        WButton miningButton = new WButton(new LiteralText("+"));
        WButton farmingButton = new WButton(new LiteralText("+"));
        WButton buildingButton = new WButton(new LiteralText("+"));

        root.add(healthButton, 77, 89, 16, 16);
        root.add(strengthButton, 77, 109, 16, 16);
        root.add(agilityButton, 77, 129, 16, 16);
        root.add(defenseButton, 77, 149, 16, 16);
        root.add(staminaButton, 77, 169, 16, 16);
        root.add(luckButton, 77, 189, 16, 16);
        root.add(archeryButton, 167, 89, 16, 16);
        root.add(tradeButton, 167, 109, 16, 16);
        root.add(smithingButton, 167, 129, 16, 16);
        root.add(miningButton, 167, 149, 16, 16);
        root.add(farmingButton, 167, 169, 16, 16);
        root.add(buildingButton, 167, 189, 16, 16);
        // Button mechanic
        setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                buildingButton, playerStatsManager);

        healthButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "health");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) + 1D);
        });
        strengthButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "strength");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + 0.2D);
        });
        agilityButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "agility");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + 0.001D);
        });
        defenseButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "defense");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR) + 0.2D);
        });
        staminaButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "stamina");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
        });
        luckButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "luck");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(playerEntity.getAttributeBaseValue(EntityAttributes.GENERIC_LUCK) + 0.05D);
        });
        archeryButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "archery");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
        });
        tradeButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "trade");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
        });
        smithingButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "smithing");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
        });
        miningButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "mining");
            // Not sure if I can use the serverpacket class here
            PlayerStatsServerPacket.syncLockedBlockList(playerStatsManager);
            // for (int i = 0; i < LevelJsonInit.MINING_LEVEL_LIST.size(); i++) {
            // if (LevelJsonInit.MINING_LEVEL_LIST.get(i) < playerStatsManager.getLevel("mining")) {
            // for (int u = 0; u < LevelJsonInit.MINING_BLOCK_LIST.get(i).size(); u++) {
            // if (!playerStatsManager.unlockedBlocks.contains(LevelJsonInit.MINING_BLOCK_LIST.get(i).get(u))) {
            // playerStatsManager.unlockedBlocks.add(LevelJsonInit.MINING_BLOCK_LIST.get(i).get(u));
            // }
            // }
            // }
            // }
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
        });
        farmingButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "farming");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
        });
        buildingButton.setOnClick(() -> {
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(playerStatsManager, "building");
            setButtonEnabled(healthButton, strengthButton, agilityButton, defenseButton, staminaButton, luckButton, archeryButton, tradeButton, smithingButton, miningButton, farmingButton,
                    buildingButton, playerStatsManager);
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
        wButton12.setEnabled(enoughPoints && playerStatsManager.getLevel("building") < ConfigInit.CONFIG.maxLevel);
    }

}