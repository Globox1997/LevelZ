package net.levelz.mixin.entity;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyVariable(method = "applyEnchantmentsToDamage", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Ljava/lang/Iterable;Lnet/minecraft/entity/damage/DamageSource;)I", shift = At.Shift.AFTER), ordinal = 0)
    private int applyEnchantmentsToDamageMixin(int original, DamageSource source, float amount) {
        if (source == DamageSource.FALL && (Object) this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
            return (int) (original + playerStatsManager.getLevel("agility") * ConfigInit.CONFIG.movementFallBonus);
        } else
            return original;
    }

    @ModifyVariable(method = "tryUseTotem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private ItemStack tryUseTotemMixin(ItemStack original) {
        if ((Object) this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            ArrayList<Object> levelList = LevelLists.totemList;
            if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
                return ItemStack.EMPTY;
            }
        }
        return original;
    }

}