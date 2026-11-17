package net.levelz.registry;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentZ {

    private final Holder<Enchantment> entry;
    private final int level;

    public EnchantmentZ(Holder<Enchantment> entry, int level) {
        this.entry = entry;
        this.level = level;
    }

    public Holder<Enchantment> getEntry() {
        return entry;
    }

    public int getLevel() {
        return level;
    }
}
