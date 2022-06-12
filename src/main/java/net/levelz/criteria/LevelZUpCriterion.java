package net.levelz.criteria;

import com.google.gson.JsonObject;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class LevelZUpCriterion extends AbstractCriterion<LevelZUpCriterion.Conditions> {
    static final Identifier ID = new Identifier("levelz:level_up");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        NumberPredicate numberPredicate = NumberPredicate.fromJson(jsonObject.get("level"));
        return new Conditions(extended, numberPredicate);
    }

    public void trigger(ServerPlayerEntity player, int level) {
        this.trigger(player, conditions -> conditions.matches(player, level));
    }

    class Conditions extends AbstractCriterionConditions {
        private final NumberPredicate numberPredicate;

        public Conditions(EntityPredicate.Extended player, NumberPredicate numberPredicate) {
            super(ID, player);
            this.numberPredicate = numberPredicate;
        }

        public boolean matches(ServerPlayerEntity player, int level) {
            return this.numberPredicate.test(player, level);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("level", this.numberPredicate.toJson());
            return jsonObject;
        }
    }

}
