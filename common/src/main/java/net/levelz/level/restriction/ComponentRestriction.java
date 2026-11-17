package net.levelz.level.restriction;

import java.util.Map;
import java.util.Objects;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
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
        DataComponentPatch.SplitResult splitResult = this.components.split();
        for (TypedDataComponent<?> component : splitResult.added()) {
            if (!Objects.equals(component.value(), stack.get(component.type()))) {
                return false;
            }
        }
        for (DataComponentType<?> removed : splitResult.removed()) {
            if (stack.get(removed) != null) {
                return false;
            }
        }
        return true;
    }

}
