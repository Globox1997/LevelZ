package net.levelz.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.levelz.access.LevelManagerAccess;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class LevelCriterion extends AbstractCriterion<LevelCriterion.Conditions> {

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> conditions.matches(player));
    }

    public record Conditions(Optional<LootContextPredicate> player, int level) implements AbstractCriterion.Conditions {

        public static final Codec<Conditions> CODEC = RecordCodecBuilder
                .create(instance -> instance
                        .group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                                Codec.INT.fieldOf("level").forGetter(Conditions::level))
                        .apply(instance, Conditions::new));

        public boolean matches(ServerPlayerEntity player) {
            return ((LevelManagerAccess) player).getLevelManager().getOverallLevel() == this.level;
        }

    }

}
