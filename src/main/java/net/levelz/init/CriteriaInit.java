package net.levelz.init;

import net.levelz.criteria.LevelCriterion;
import net.levelz.criteria.SkillCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class CriteriaInit {

    public static final ScoreboardCriterion LEVELZ = ScoreboardCriterion.create("levelz");

    public static final LevelCriterion LEVEL_UP = Criteria.register("levelz:level", new LevelCriterion());
    public static final SkillCriterion SKILL_UP = Criteria.register("levelz:skill",new SkillCriterion());

    public static void init() {
    }

}
