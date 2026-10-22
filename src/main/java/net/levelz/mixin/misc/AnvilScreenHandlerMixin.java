package net.levelz.mixin.misc;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.util.BonusHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow
    @Mutable
    @Final
    private Property levelCost;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 4))
    private void updateResultMixin(CallbackInfo info) {
        if (this.levelCost.get() > 1) {
            this.levelCost.set(BonusHelper.anvilXpDiscountBonus(this.player, this.levelCost.get()));
        }
    }

    @ModifyArg(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 4), index = 1)
    private ItemStack updateResultEnchantmentMixin(ItemStack result) {
        if (result.isEmpty() || this.player.isCreative() || result.isOf(Items.ENCHANTED_BOOK) || !this.input.getStack(1).isOf(Items.ENCHANTED_BOOK)) {
            return result;
        }
        LevelManager levelManager = ((LevelManagerAccess) this.player).getLevelManager();
        ItemStack base = this.input.getStack(0);
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : EnchantmentHelper.getEnchantments(result).getEnchantmentEntries()) {
            int level = entry.getIntValue();
            if (level > EnchantmentHelper.getLevel(entry.getKey(), base) && !levelManager.hasRequiredEnchantmentLevel(entry.getKey(), level)) {
                this.levelCost.set(0);
                return ItemStack.EMPTY;
            }
        }
        return result;
    }

    @Inject(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"), require = 0)
    private void onTakeOutputMixin(PlayerEntity playerEntity, ItemStack stack, CallbackInfo ci) {
        if (BonusHelper.anvilXpChanceBonus(playerEntity)) {
            this.levelCost.set(0);
        }
    }
}