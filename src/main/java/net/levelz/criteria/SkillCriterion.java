package net.levelz.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import java.util.Optional;

public class SkillCriterion extends SimpleCriterionTrigger<SkillCriterion.Conditions> {

    @Override
    public Codec<SkillCriterion.Conditions> codec() {
        return SkillCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, String skillName, int skillLevel) {
        this.trigger(player, conditions -> conditions.matches(player, skillName, skillLevel));
    }

    public record Conditions(Optional<Holder<LootItemCondition>> player, String skillName, int skillLevel) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<SkillCriterion.Conditions> CODEC = RecordCodecBuilder
                .create(instance -> instance
                        .group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(SkillCriterion.Conditions::player),
                                Codec.STRING.fieldOf("skill_name").forGetter(SkillCriterion.Conditions::skillName), Codec.INT.fieldOf("skill_level").forGetter(SkillCriterion.Conditions::skillLevel))
                        .apply(instance, SkillCriterion.Conditions::new));

        public boolean matches(ServerPlayer player, String skillName, int skillLevel) {
            if (!skillName.equals(this.skillName)) {
                return false;
            }
            return skillLevel == this.skillLevel;
        }
    }

}
