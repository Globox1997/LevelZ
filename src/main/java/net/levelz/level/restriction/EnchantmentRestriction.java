package net.levelz.level.restriction;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentRestriction {

    private final Holder<Enchantment> enchantment;
    private final Map<Integer, Map<Integer, Integer>> skillLevelRestrictions;

    public EnchantmentRestriction(Holder<Enchantment> enchantment, Map<Integer, Map<Integer, Integer>> skillLevelRestrictions) {
        this.enchantment = enchantment;
        this.skillLevelRestrictions = skillLevelRestrictions;
    }

    public Holder<Enchantment> getEnchantment() {
        return enchantment;
    }

    public Map<Integer, Map<Integer, Integer>> getSkillLevelRestrictions() {
        return skillLevelRestrictions;
    }

}
