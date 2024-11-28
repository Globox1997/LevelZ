package net.levelz.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnchantmentRegistry {

    public static final Map<Integer, EnchantmentZ> ENCHANTMENTS = new HashMap<>();
    public static final Map<String, Integer> INDEX_ENCHANTMENTS = new HashMap<>();

    public static boolean containsId(RegistryEntry<Enchantment> enchantment, int level) {
        return containsId(enchantment.toString(), level);
    }

    public static boolean containsId(Identifier identifier, int level) {
        return containsId(identifier.toString(), level);
    }

    public static boolean containsId(String enchantment, int level) {
        return INDEX_ENCHANTMENTS.containsKey(enchantment + level);
    }

    public static EnchantmentZ getEnchantmentZ(int key) {
        return ENCHANTMENTS.get(key);
    }

    public static int getId(RegistryEntry<Enchantment> enchantment, int level) {
        return getId(enchantment.getIdAsString(), level);
    }

    public static int getId(Identifier identifier, int level) {
        return getId(identifier.toString(), level);
    }

    public static int getId(String enchantment, int level) {
        return getId(enchantment + level);
    }

    private static int getId(String enchantment) {
        if (INDEX_ENCHANTMENTS.containsKey(enchantment)) {
            return INDEX_ENCHANTMENTS.get(enchantment);
        }
        return -1;
    }

    public static void updateEnchantments(RegistryWrapper.WrapperLookup wrapperLookup) {
        ENCHANTMENTS.clear();
        INDEX_ENCHANTMENTS.clear();
        Optional<RegistryWrapper.Impl<Enchantment>> wrapper = wrapperLookup.getOptionalWrapper(RegistryKeys.ENCHANTMENT);
        for (RegistryWrapper.Impl<Enchantment> enchantmentImpl : wrapper.stream().toList()) {
            for (RegistryEntry.Reference<Enchantment> enchantment : enchantmentImpl.streamEntries().toList()) {
                for (int i = 1; i <= enchantment.value().getMaxLevel(); i++) {
                    INDEX_ENCHANTMENTS.put(enchantment.getIdAsString() + i, ENCHANTMENTS.size());
                    ENCHANTMENTS.put(ENCHANTMENTS.size(), new EnchantmentZ(enchantment, i));
                }
            }
        }
    }

}
