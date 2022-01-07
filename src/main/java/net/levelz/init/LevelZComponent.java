package net.levelz.init;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.levelz.config.LevelRule;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Identifier;

public class LevelZComponent implements ScoreboardComponentInitializer {
    private static final ComponentKey<LevelRule> LEVEL_RULE = ComponentRegistry.getOrCreate(new Identifier("levelz", "rule"), LevelRule.class);

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(LEVEL_RULE, (scoreboard, server) -> new LevelRule());
    }

    public static LevelRule getLevelRule(Scoreboard scoreboard) {
        return LEVEL_RULE.get(scoreboard);
    }
}
