package net.levelz.init;

import net.levelz.criteria.LevelCriterion;
import net.levelz.criteria.SkillCriterion;
import net.levelz.LevelzMain;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class CriteriaInit {

    public static final ObjectiveCriteria LEVELZ = ObjectiveCriteria.registerCustom("levelz");

    public static final LevelCriterion LEVEL_UP = Registry.register(BuiltInRegistries.TRIGGER_TYPES, LevelzMain.identifierOf("level"), new LevelCriterion());
    public static final SkillCriterion SKILL_UP = Registry.register(BuiltInRegistries.TRIGGER_TYPES, LevelzMain.identifierOf("skill"), new SkillCriterion());

    public static void init() {
    }

}
