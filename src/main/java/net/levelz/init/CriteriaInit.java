package net.levelz.init;

import net.levelz.criteria.LevelCriterion;
import net.levelz.criteria.SkillCriterion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class CriteriaInit {

    public static final ObjectiveCriteria LEVELZ = ObjectiveCriteria.registerCustom("levelz");

    public static final LevelCriterion LEVEL_UP = CriteriaTriggers.register("levelz:level", new LevelCriterion());
    public static final SkillCriterion SKILL_UP = CriteriaTriggers.register("levelz:skill",new SkillCriterion());

    public static void init() {
    }

}
