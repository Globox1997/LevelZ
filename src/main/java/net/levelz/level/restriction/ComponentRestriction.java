package net.levelz.level.restriction;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ComponentRestriction extends PlayerRestriction {

    private final ComponentChanges components;

    public ComponentRestriction(int id, ComponentChanges components, Map<Integer, Integer> skillLevelRestrictions) {
        super(id, skillLevelRestrictions);
        this.components = components;
    }

    public ComponentChanges getComponents() {
        return components;
    }

    public boolean matches(ItemStack stack) {
        if (Registries.ITEM.getRawId(stack.getItem()) != this.getId()) {
            return false;
        }
        for (Map.Entry<ComponentType<?>, Optional<?>> entry : this.components.entrySet()) {
            Object value = stack.get(entry.getKey());
            if (entry.getValue().isPresent() ? !Objects.equals(entry.getValue().get(), value) : value != null) {
                return false;
            }
        }
        return true;
    }

}
