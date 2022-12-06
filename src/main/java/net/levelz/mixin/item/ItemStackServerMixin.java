package net.levelz.mixin.item;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.Skill;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

@Mixin(ItemStack.class)
public class ItemStackServerMixin {

    @ModifyVariable(method = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I"), ordinal = 1)
    private int damageMixin(int original, int amount, Random random, @Nullable ServerPlayerEntity player) {
        if (player != null) {
            if ((float) ((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.SMITHING) * ConfigInit.CONFIG.smithingToolChance > random.nextFloat()) {
                return original + 1;
            }
        }
        return original;
    }

}
