package net.levelz.restriction;

import net.minecraft.entity.EntityType;

import java.util.Map;

@Deprecated
public class EntityRestriction {

    private final EntityType<?> entityType;
    private final Map<Integer, Integer> skillLevelRestrictions;

    public EntityRestriction(EntityType<?> entityType, Map<Integer, Integer> skillLevelRestrictions) {
        this.entityType = entityType;
        this.skillLevelRestrictions = skillLevelRestrictions;
    }
}
