package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @ModifyVariable(method = "getEquipmentLevel", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;"), ordinal = 0)
    private static int getEquipmentLevelMixin(int original, Enchantment enchantment, LivingEntity entity) {
        if (entity instanceof PlayerEntity && original != 0) {
            if ((float) ((PlayerStatsManagerAccess) entity).getPlayerStatsManager((PlayerEntity) entity).getLevel("alchemy") * ConfigInit.CONFIG.alchemyEnchantmentChance > entity.world.random
                    .nextFloat()) {
                return original += 1;
            }
        }
        return original;
    }
}
