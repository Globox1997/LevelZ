package net.levelz.mixin.misc;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(TradeOffers.ProcessItemFactory.class)
public class ProcessItemFactoryMixin {

    @Inject(method = "method_59950", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;applyEnchantmentProvider(Lnet/minecraft/item/ItemStack;Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/util/math/random/Random;)V", shift = At.Shift.AFTER), cancellable = true)
    private static void method_59950Mixin(ItemStack itemStack, World world, Entity entity, Random random, RegistryKey<EnchantmentProvider> key, CallbackInfo info) {
        if (entity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemStack);

            if (!itemEnchantmentsComponent.isEmpty()) {
                boolean hasAllRequiredLevels = true;
                Map<RegistryEntry<Enchantment>, Integer> enchantments = new HashMap<>();
                for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
                    if (levelManager.hasRequiredEnchantmentLevel(entry.getKey(), entry.getIntValue())) {
                        enchantments.put(entry.getKey(), entry.getIntValue());
                    } else {
                        hasAllRequiredLevels = false;
                    }
                }
                if (!hasAllRequiredLevels) {
                    ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
                    for (Map.Entry<RegistryEntry<Enchantment>, Integer> entry : enchantments.entrySet()) {
                        builder.add(entry.getKey(), entry.getValue());
                    }
                    itemStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
                }
            }
        }
    }
}
