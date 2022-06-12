package net.levelz.init;

import net.fabricmc.fabric.mixin.object.builder.CriteriaAccessor;
import net.levelz.criteria.LevelZUpCriterion;

public class CriteriaInit {

    public static final LevelZUpCriterion LEVEL_UP = CriteriaAccessor.callRegister(new LevelZUpCriterion());

    public static void init() {
    }

}
