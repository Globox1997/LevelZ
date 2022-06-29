package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.scoreboard.ScoreboardCriterion;

@Mixin(ScoreboardCriterion.class)
public interface ScoreboardCriterionAccessor {

    @Invoker("create")
    static ScoreboardCriterion callCreate(String name) {
        throw new AssertionError("Untransformed Accessor!");
    }

}
