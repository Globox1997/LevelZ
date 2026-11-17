package net.levelz.level;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class SkillAttribute {

    private final int id;
    private final Holder<Attribute> attibute;
    private final float baseValue;
    private final float levelValue;
    private final AttributeModifier.Operation operation;

    public SkillAttribute(int id, Holder<Attribute> attibute, float baseValue, float levelValue, AttributeModifier.Operation operation) {
        this.id = id;
        this.attibute = attibute;
        this.baseValue = baseValue;
        this.levelValue = levelValue;
        this.operation = operation;
    }

    public int getId() {
        return id;
    }

    public Holder<Attribute> getAttibute() {
        return attibute;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public float getLevelValue() {
        return levelValue;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

}
