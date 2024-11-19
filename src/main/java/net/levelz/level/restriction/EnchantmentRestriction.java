package net.levelz.level.restriction;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Map;

public class EnchantmentRestriction {

    private final RegistryEntry<Enchantment> enchantment;
    private final Map<Integer, Map<Integer, Integer>> skillLevelRestrictions;

    public EnchantmentRestriction(RegistryEntry<Enchantment> enchantment, Map<Integer, Map<Integer, Integer>> skillLevelRestrictions) {
        this.enchantment = enchantment;
        this.skillLevelRestrictions = skillLevelRestrictions;
    }

    public RegistryEntry<Enchantment> getEnchantment() {
        return enchantment;
    }

    public Map<Integer, Map<Integer, Integer>> getSkillLevelRestrictions() {
        return skillLevelRestrictions;
    }

}
