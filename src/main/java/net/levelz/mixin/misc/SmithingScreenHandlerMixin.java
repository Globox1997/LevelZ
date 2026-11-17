package net.levelz.mixin.misc;

import net.levelz.access.LevelManagerAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingScreenHandlerMixin extends ItemCombinerMenu {

    public SmithingScreenHandlerMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(type, syncId, playerInventory, context, slotDefinition);
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void updateResultMixin(CallbackInfo info) {
        ItemStack result = this.resultSlots.getItem(0);
        if (!result.isEmpty() && !this.player.isCreative() && !((LevelManagerAccess) this.player).getLevelManager().hasRequiredCraftingLevel(result.getItem())) {
            this.resultSlots.setRecipeUsed(null);
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }
    }
}
