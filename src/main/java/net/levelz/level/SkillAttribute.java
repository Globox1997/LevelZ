package net.levelz.level;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;

public class SkillAttribute {

    private final int id;
    private final RegistryEntry<EntityAttribute> attibute;
    private final float baseValue;
    private final float levelValue;
    private final EntityAttributeModifier.Operation operation;

    public SkillAttribute(int id, RegistryEntry<EntityAttribute> attibute, float baseValue, float levelValue, EntityAttributeModifier.Operation operation) {
        this.id = id;
        this.attibute = attibute;
        this.baseValue = baseValue;
        this.levelValue = levelValue;
        this.operation = operation;
    }

    public int getId() {
        return id;
    }

    public RegistryEntry<EntityAttribute> getAttibute() {
        return attibute;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public float getLevelValue() {
        return levelValue;
    }

    public EntityAttributeModifier.Operation getOperation() {
        return operation;
    }

}
