package net.levelz.init;

import net.fabricmc.fabric.mixin.object.builder.CriteriaAccessor;
import net.levelz.criteria.LevelZUpCriterion;
import net.levelz.mixin.misc.ScoreboardCriterionAccessor;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class CriteriaInit {

    public static final LevelZUpCriterion LEVEL_UP = CriteriaAccessor.callRegister(new LevelZUpCriterion());
    public static final ScoreboardCriterion LEVELZ = ScoreboardCriterionAccessor.callCreate("levelz");

    public static void init() {
    }

}
