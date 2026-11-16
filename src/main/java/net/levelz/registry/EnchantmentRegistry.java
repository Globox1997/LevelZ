package net.levelz.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantmentRegistry {

    public static final Map<Integer, EnchantmentZ> ENCHANTMENTS = new HashMap<>();
    public static final Map<String, Integer> INDEX_ENCHANTMENTS = new HashMap<>();

    private static final Map<RegistryEntry<Enchantment>, int[]> ID_CACHE = new ConcurrentHashMap<>();

    public static boolean containsId(RegistryEntry<Enchantment> enchantment, int level) {
        return getId(enchantment, level) >= 0;
    }

    public static boolean containsId(Identifier identifier, int level) {
        return containsId(identifier.toString(), level);
    }

    public static boolean containsId(String enchantment, int level) {
        return INDEX_ENCHANTMENTS.containsKey(getKey(enchantment, level));
    }

    public static EnchantmentZ getEnchantmentZ(int key) {
        return ENCHANTMENTS.get(key);
    }

    public static int getId(RegistryEntry<Enchantment> enchantment, int level) {
        int[] ids = ID_CACHE.computeIfAbsent(enchantment, EnchantmentRegistry::createIds);
        return level > 0 && level < ids.length ? ids[level] : -1;
    }

    private static int[] createIds(RegistryEntry<Enchantment> enchantment) {
        String enchantmentId = enchantment.getIdAsString();
        int maxLevel = enchantment.value().getMaxLevel();
        int[] ids = new int[maxLevel + 1];
        ids[0] = -1;
        for (int level = 1; level <= maxLevel; level++) {
            ids[level] = getId(enchantmentId, level);
        }
        return ids;
    }

    public static void clear() {
        ENCHANTMENTS.clear();
        INDEX_ENCHANTMENTS.clear();
        ID_CACHE.clear();
    }

    public static int getId(Identifier identifier, int level) {
        return getId(identifier.toString(), level);
    }

    public static int getId(String enchantment, int level) {
        return getId(getKey(enchantment, level));
    }

    private static String getKey(String enchantment, int level) {
        return enchantment + "#" + level;
    }

    private static int getId(String enchantment) {
        if (INDEX_ENCHANTMENTS.containsKey(enchantment)) {
            return INDEX_ENCHANTMENTS.get(enchantment);
        }
        return -1;
    }

    public static void updateEnchantments(RegistryWrapper.WrapperLookup wrapperLookup) {
        clear();
        Optional<RegistryWrapper.Impl<Enchantment>> wrapper = wrapperLookup.getOptionalWrapper(RegistryKeys.ENCHANTMENT);
        for (RegistryWrapper.Impl<Enchantment> enchantmentImpl : wrapper.stream().toList()) {
            for (RegistryEntry.Reference<Enchantment> enchantment : enchantmentImpl.streamEntries().toList()) {
                for (int i = 1; i <= enchantment.value().getMaxLevel(); i++) {
                    INDEX_ENCHANTMENTS.put(getKey(enchantment.getIdAsString(), i), ENCHANTMENTS.size());
                    ENCHANTMENTS.put(ENCHANTMENTS.size(), new EnchantmentZ(enchantment, i));
                }
            }
        }
        ID_CACHE.clear();
    }

}
