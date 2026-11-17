package net.levelz.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentRegistry {

    public static final Map<Integer, EnchantmentZ> ENCHANTMENTS = new HashMap<>();
    public static final Map<String, Integer> INDEX_ENCHANTMENTS = new HashMap<>();

    private static final Map<Holder<Enchantment>, int[]> ID_CACHE = new ConcurrentHashMap<>();

    public static boolean containsId(Holder<Enchantment> enchantment, int level) {
        return getId(enchantment, level) >= 0;
    }

    public static boolean containsId(ResourceLocation identifier, int level) {
        return containsId(identifier.toString(), level);
    }

    public static boolean containsId(String enchantment, int level) {
        return INDEX_ENCHANTMENTS.containsKey(getKey(enchantment, level));
    }

    public static EnchantmentZ getEnchantmentZ(int key) {
        return ENCHANTMENTS.get(key);
    }

    public static int getId(Holder<Enchantment> enchantment, int level) {
        int[] ids = ID_CACHE.computeIfAbsent(enchantment, EnchantmentRegistry::createIds);
        return level > 0 && level < ids.length ? ids[level] : -1;
    }

    private static int[] createIds(Holder<Enchantment> enchantment) {
        String enchantmentId = enchantment.getRegisteredName();
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

    public static int getId(ResourceLocation identifier, int level) {
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

    public static void updateEnchantments(HolderLookup.Provider wrapperLookup) {
        clear();
        Optional<HolderLookup.RegistryLookup<Enchantment>> wrapper = wrapperLookup.lookup(Registries.ENCHANTMENT);
        for (HolderLookup.RegistryLookup<Enchantment> enchantmentImpl : wrapper.stream().toList()) {
            for (Holder.Reference<Enchantment> enchantment : enchantmentImpl.listElements().toList()) {
                for (int i = 1; i <= enchantment.value().getMaxLevel(); i++) {
                    INDEX_ENCHANTMENTS.put(getKey(enchantment.getRegisteredName(), i), ENCHANTMENTS.size());
                    ENCHANTMENTS.put(ENCHANTMENTS.size(), new EnchantmentZ(enchantment, i));
                }
            }
        }
        ID_CACHE.clear();
    }

}
