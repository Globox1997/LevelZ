package net.levelz.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

public class EnchantmentZ {

    private final RegistryEntry<Enchantment> entry;
    private final int level;

    public EnchantmentZ(RegistryEntry<Enchantment> entry, int level) {
        this.entry = entry;
        this.level = level;
    }

    public RegistryEntry<Enchantment> getEntry() {
        return entry;
    }

    public int getLevel() {
        return level;
    }
}
