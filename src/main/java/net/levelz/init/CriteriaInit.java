package net.levelz.init;

import net.fabricmc.fabric.mixin.object.builder.CriteriaAccessor;
import net.levelz.criteria.LevelZCriterion;
import net.levelz.criteria.SkillCriterion;
import net.levelz.mixin.misc.ScoreboardCriterionAccessor;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class CriteriaInit {

    public static final LevelZCriterion LEVEL_UP = CriteriaAccessor.callRegister(new LevelZCriterion());
    public static final SkillCriterion SKILL_UP = CriteriaAccessor.callRegister(new SkillCriterion());
    public static final ScoreboardCriterion LEVELZ = ScoreboardCriterionAccessor.callCreate("levelz");

    public static void init() {
    }

}
