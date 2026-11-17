package net.levelz.util;

import net.levelz.LevelzMain;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.level.SkillAttribute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class LevelHelper {

    public static void updateSkill(ServerPlayer serverPlayerEntity, Skill skill) {
        LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).getLevelManager();
        for (SkillAttribute skillAttribute : skill.getAttributes()) {
            if (serverPlayerEntity.getAttribute(skillAttribute.getAttibute()) != null) {
                if (skillAttribute.getBaseValue() > -9999.0f) {
                    serverPlayerEntity.getAttribute(skillAttribute.getAttibute()).setBaseValue(skillAttribute.getBaseValue());
                }
                ResourceLocation identifier = LevelzMain.identifierOf(skill.getKey());
                if (serverPlayerEntity.getAttribute(skillAttribute.getAttibute()).hasModifier(identifier)) {
                    serverPlayerEntity.getAttribute(skillAttribute.getAttibute()).removeModifier(identifier);
                }
                serverPlayerEntity.getAttribute(skillAttribute.getAttibute()).addTransientModifier(new AttributeModifier(identifier, skillAttribute.getLevelValue() * levelManager.getSkillLevel(skill.getId()), skillAttribute.getOperation()));
            }
        }
    }
}
