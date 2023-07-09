package net.levelz.init;

import net.levelz.criteria.LevelZCriterion;
import net.levelz.criteria.SkillCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class CriteriaInit {

    public static final LevelZCriterion LEVEL_UP = Criteria.register(new LevelZCriterion());
    public static final SkillCriterion SKILL_UP = Criteria.register(new SkillCriterion());
    public static final ScoreboardCriterion LEVELZ = ScoreboardCriterion.create("levelz");

    public static void init() {
    }

}
