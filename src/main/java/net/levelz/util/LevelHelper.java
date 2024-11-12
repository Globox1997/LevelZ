package net.levelz.util;

import net.levelz.LevelzMain;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.level.SkillAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public class LevelHelper {


    // Todo: May check health attribute to update to client?

    public static void updateSkill(ServerPlayerEntity serverPlayerEntity, Skill skill) {
        LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).getLevelManager();
        for (SkillAttribute skillAttribute : skill.getAttributes()) {
            if (serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()) != null) {
                double baseValue = serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()).getBaseValue();
                if (skillAttribute.getBaseValue() > 0.0f) {
                    baseValue = skillAttribute.getBaseValue();
                }
                if (skillAttribute.getOperation().equals(EntityAttributeModifier.Operation.ADD_VALUE)) {
                    serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()).setBaseValue(baseValue + skillAttribute.getLevelValue() * levelManager.getSkillLevel(skill.getId()));
                } else if (skillAttribute.getOperation().equals(EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE)) {
                    serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()).setBaseValue(baseValue * skillAttribute.getLevelValue() * levelManager.getSkillLevel(skill.getId()));
                } else if (skillAttribute.getOperation().equals(EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)) {
                    serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()).setBaseValue(baseValue);
                    serverPlayerEntity.getAttributeInstance(skillAttribute.getAttibute()).addPersistentModifier(new EntityAttributeModifier(LevelzMain.identifierOf(skillAttribute.getAttibute().getIdAsString()), skillAttribute.getLevelValue() * levelManager.getSkillLevel(skill.getId()), skillAttribute.getOperation()));
                }

                // Todo: Edge case
                if (skillAttribute.getAttibute().equals(EntityAttributes.GENERIC_MAX_HEALTH)) {
//                    serverPlayerEntity.setHealth(player.getHealth() + (float) ConfigInit.CONFIG.healthBonus * level);
                }
            }
        }
    }
}
