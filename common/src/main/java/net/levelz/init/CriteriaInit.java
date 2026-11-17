package net.levelz.init;

import net.levelz.criteria.LevelCriterion;
import net.levelz.criteria.SkillCriterion;
import net.levelz.LevelzMain;
import net.levelz.mixin.misc.ObjectiveCriteriaAccessor;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.function.BiConsumer;

public class CriteriaInit {

    public static final ObjectiveCriteria LEVELZ = ObjectiveCriteriaAccessor.callRegisterCustom("levelz");

    public static LevelCriterion LEVEL_UP;
    public static SkillCriterion SKILL_UP;

    public static void register(BiConsumer<Identifier, CriterionTrigger<?>> registrar) {
        LEVEL_UP = new LevelCriterion();
        registrar.accept(LevelzMain.identifierOf("level"), LEVEL_UP);
        SKILL_UP = new SkillCriterion();
        registrar.accept(LevelzMain.identifierOf("skill"), SKILL_UP);
    }

}
