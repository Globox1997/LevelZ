package net.levelz.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.levelz.access.LevelManagerAccess;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import java.util.Optional;

public class LevelCriterion extends SimpleCriterionTrigger<LevelCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, conditions -> conditions.matches(player));
    }

    public record Conditions(Optional<Holder<LootItemCondition>> player, int level) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Conditions> CODEC = RecordCodecBuilder
                .create(instance -> instance
                        .group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                                Codec.INT.fieldOf("level").forGetter(Conditions::level))
                        .apply(instance, Conditions::new));

        public boolean matches(ServerPlayer player) {
            return ((LevelManagerAccess) player).getLevelManager().getOverallLevel() == this.level;
        }

    }

}
