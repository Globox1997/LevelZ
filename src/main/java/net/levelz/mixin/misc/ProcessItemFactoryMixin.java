package net.levelz.mixin.misc;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(VillagerTrades.ItemsAndEmeraldsToItems.class)
public class ProcessItemFactoryMixin {

    @Inject(method = "method_59950", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;enchantItemFromProvider(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/util/RandomSource;)V", shift = At.Shift.AFTER), cancellable = true)
    private static void method_59950Mixin(ItemStack itemStack, Level world, Entity entity, RandomSource random, ResourceKey<EnchantmentProvider> key, CallbackInfo info) {
        if (entity instanceof Player playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            ItemEnchantments itemEnchantmentsComponent = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);

            if (!itemEnchantmentsComponent.isEmpty()) {
                boolean hasAllRequiredLevels = true;
                Map<Holder<Enchantment>, Integer> enchantments = new HashMap<>();
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantmentsComponent.entrySet()) {
                    if (levelManager.hasRequiredEnchantmentLevel(entry.getKey(), entry.getIntValue())) {
                        enchantments.put(entry.getKey(), entry.getIntValue());
                    } else {
                        hasAllRequiredLevels = false;
                    }
                }
                if (!hasAllRequiredLevels) {
                    ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                    for (Map.Entry<Holder<Enchantment>, Integer> entry : enchantments.entrySet()) {
                        builder.upgrade(entry.getKey(), entry.getValue());
                    }
                    itemStack.set(DataComponents.ENCHANTMENTS, builder.toImmutable());
                }
            }
        }
    }
}
