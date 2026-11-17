package net.levelz.mixin.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.util.BonusHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {

    @Shadow
    @Mutable
    @Final
    private DataSlot cost;

    public AnvilScreenHandlerMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(type, syncId, playerInventory, context, slotDefinition);
    }

    @WrapOperation(method = {"createResult()V", "createResultInternal()V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    private void updateResultMixin(ResultContainer instance, int slot, ItemStack result, Operation<Void> original) {
        if (!result.isEmpty()) {
            result = this.updateResultEnchantment(result);
            if (!result.isEmpty() && this.cost.get() > 1) {
                this.cost.set(BonusHelper.anvilXpDiscountBonus(this.player, this.cost.get()));
            }
        }
        original.call(instance, slot, result);
    }

    @Unique
    private ItemStack updateResultEnchantment(ItemStack result) {
        if (this.player.isCreative() || result.is(Items.ENCHANTED_BOOK) || !this.inputSlots.getItem(1).is(Items.ENCHANTED_BOOK)) {
            return result;
        }
        LevelManager levelManager = ((LevelManagerAccess) this.player).getLevelManager();
        ItemStack base = this.inputSlots.getItem(0);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(result).entrySet()) {
            int level = entry.getIntValue();
            if (level > EnchantmentHelper.getItemEnchantmentLevel(entry.getKey(), base) && !levelManager.hasRequiredEnchantmentLevel(entry.getKey(), level)) {
                this.cost.set(0);
                return ItemStack.EMPTY;
            }
        }
        return result;
    }

    @Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;get()I"), require = 0)
    private void onTakeOutputMixin(Player playerEntity, ItemStack stack, CallbackInfo ci) {
        if (BonusHelper.anvilXpChanceBonus(playerEntity)) {
            this.cost.set(0);
        }
    }
}