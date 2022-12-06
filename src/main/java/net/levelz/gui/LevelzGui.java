package net.levelz.gui;

import com.google.common.collect.Multimap;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.RenderInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class LevelzGui extends LightweightGuiDescription {

    private final PlayerEntity playerEntity;

    public LevelzGui(MinecraftClient client) {
        playerEntity = client.player;

        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(200, 200);
        // Top label
        WLabel topLlabel = new WLabel(Text.translatable("text.levelz.gui.title", playerEntity.getName().getString()));
        root.add(topLlabel, 80, 7);
        // Small icons
        WSprite lifeIcon = new WSprite(RenderInit.GUI_ICONS, 0F, 0F, 0.03515625F, 0.03515625F);
        WSprite protectionIcon = new WSprite(RenderInit.GUI_ICONS, 0.03515625F, 0F, 0.0703125F, 0.03515625F);
        WSprite speedIcon = new WSprite(RenderInit.GUI_ICONS, 0.0703125F, 0F, 0.10546875F, 0.03515625F);
        WSprite damageIcon = new WSprite(RenderInit.GUI_ICONS, 0.10546875F, 0F, 0.140625F, 0.03515625F);
        WSprite foodIcon = new WSprite(RenderInit.GUI_ICONS, 0.140625F, 0F, 0.17578125F, 0.03515625F);
        WSprite fortuneIcon = new WSprite(RenderInit.GUI_ICONS, 0.17578125F, 0F, 0.2109375F, 0.03515625F);

        root.add(lifeIcon, 58, 21, 10, 10);
        root.add(protectionIcon, 58, 34, 10, 10);
        root.add(damageIcon, 108, 21, 10, 10);
        root.add(speedIcon, 108, 34, 10, 10);
        root.add(foodIcon, 155, 21, 10, 10);
        root.add(fortuneIcon, 155, 34, 10, 10);

        // Small icon labels
        WDynamicLabel lifeLabel = new WDynamicLabel(() -> "" + Math.round(playerEntity.getHealth()));
        WDynamicLabel protectionLabel = new WDynamicLabel(
                () -> "" + BigDecimal.valueOf(playerEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR)).setScale(2, RoundingMode.HALF_DOWN).floatValue());
        WDynamicLabel damageLabel = new WDynamicLabel(this::getDamageLabel);
        WDynamicLabel speedLabel = new WDynamicLabel(
                () -> "" + BigDecimal.valueOf(playerEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 10D).setScale(2, RoundingMode.HALF_DOWN).floatValue());
        WDynamicLabel foodLabel = new WDynamicLabel(() -> "" + Math.round(playerEntity.getHungerManager().getFoodLevel()));
        WDynamicLabel fortuneLabel = new WDynamicLabel(() -> "" + BigDecimal.valueOf(playerEntity.getAttributeValue(EntityAttributes.GENERIC_LUCK)).setScale(2, RoundingMode.HALF_DOWN).floatValue());

        // experience bar
        root.add(new PlayerStatsPanel(client, 130), 58, 50);

        root.add(lifeLabel, 74, 22);
        root.add(protectionLabel, 74, 36);
        root.add(damageLabel, 124, 22);
        root.add(speedLabel, 124, 36);
        root.add(foodLabel, 171, 22);
        root.add(fortuneLabel, 171, 36);

        // Info button
        ZWSprite infoIcon = new ZWSprite("info", null, 1);
        infoIcon.addText(Text.translatable("text.levelz.more_info").getString());
        infoIcon.addText(Text.translatable("text.levelz.gui.level_up_skill.tooltip").getString().split("\n"));
        root.add(infoIcon, 178, 73, 11, 13);

        if (!LevelLists.craftingItemList.isEmpty()) {
            ZWSprite craftingIcon = new ZWSprite("crafting", client, 3);
            craftingIcon.addText(Text.translatable("text.levelz.crafting_info").getString());
            root.add(craftingIcon, 180, 5, 15, 13);
        }

        // Skills
        int i = 0;
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < Skill.values().length / 2; y++) {
                root.add(new SkillPanel(client, Skill.values()[i++], 80, 16), 15 + x * 90, 90 + y * 20);
            }
        }

        // bottom blank
        root.add(new WLabel(Text.empty()), 0, 206, 1, 10);
        root.validate(this);
    }

    private String getDamageLabel() {
        float damage = 0.0F;
        Item item = playerEntity.getMainHandStack().getItem();
        ArrayList<Object> levelList = LevelLists.customItemList;
        if (!levelList.isEmpty() && PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, Registry.ITEM.getId(item).toString(), false)) {
            if (item instanceof SwordItem swordItem)
                damage = swordItem.getAttackDamage();
            else if (item instanceof MiningToolItem miningToolItem)
                damage = miningToolItem.getAttackDamage();
            else if (!item.getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty() && item.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
                Multimap<EntityAttribute, EntityAttributeModifier> multimap = item.getAttributeModifiers(EquipmentSlot.MAINHAND);
                for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : multimap.entries()) {
                    if (entry.getKey().equals(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
                        damage = (float) entry.getValue().getValue();
                        break;
                    }
                }
            }
        } else if (item instanceof ToolItem toolItem) {
            levelList = null;
            if (item instanceof SwordItem) {
                levelList = LevelLists.swordList;
            } else if (item instanceof AxeItem)
                levelList = LevelLists.axeList;
            else if (item instanceof HoeItem)
                levelList = LevelLists.hoeList;
            else if (item instanceof PickaxeItem || item instanceof ShovelItem)
                levelList = LevelLists.toolList;
            if (levelList != null)
                if (PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, toolItem.getMaterial().toString().toLowerCase(), false)) {
                    if (item instanceof SwordItem swordItem)
                        damage = swordItem.getAttackDamage();
                    else if (item instanceof MiningToolItem miningToolItem)
                        damage = miningToolItem.getAttackDamage();
                }
        }

        damage += playerEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        return String.valueOf(BigDecimal.valueOf(damage).setScale(2, RoundingMode.HALF_DOWN).floatValue());
    }

}