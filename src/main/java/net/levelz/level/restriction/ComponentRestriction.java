package net.levelz.level.restriction;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class ComponentRestriction extends PlayerRestriction {

    private final DataComponentPatch components;

    public ComponentRestriction(int id, DataComponentPatch components, Map<Integer, Integer> skillLevelRestrictions) {
        super(id, skillLevelRestrictions);
        this.components = components;
    }

    public DataComponentPatch getComponents() {
        return components;
    }

    public boolean matches(ItemStack stack) {
        if (BuiltInRegistries.ITEM.getId(stack.getItem()) != this.getId()) {
            return false;
        }
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : this.components.entrySet()) {
            Object value = stack.get(entry.getKey());
            if (entry.getValue().isPresent() ? !Objects.equals(entry.getValue().get(), value) : value != null) {
                return false;
            }
        }
        return true;
    }

}
