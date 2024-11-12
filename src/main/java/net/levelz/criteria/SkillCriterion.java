package net.levelz.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class SkillCriterion extends AbstractCriterion<SkillCriterion.Conditions> {

    @Override
    public Codec<SkillCriterion.Conditions> getConditionsCodec() {
        return SkillCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, String skillName, int skillLevel) {
        this.trigger(player, conditions -> conditions.matches(player, skillName, skillLevel));
    }

    public record Conditions(Optional<LootContextPredicate> player, String skillName, int skillLevel) implements AbstractCriterion.Conditions {

        public static final Codec<SkillCriterion.Conditions> CODEC = RecordCodecBuilder
                .create(instance -> instance
                        .group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(SkillCriterion.Conditions::player),
                                Codec.STRING.fieldOf("skill_name").forGetter(SkillCriterion.Conditions::skillName), Codec.INT.fieldOf("skill_level").forGetter(SkillCriterion.Conditions::skillLevel))
                        .apply(instance, SkillCriterion.Conditions::new));

        public boolean matches(ServerPlayerEntity player, String skillName, int skillLevel) {
            if (!skillName.equals(this.skillName)) {
                return false;
            }
            return skillLevel == this.skillLevel;
        }
    }

}
