package net.levelz.mixin.misc;

import net.levelz.access.LevelManagerAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(SmithingMenu.class)
public abstract class SmithingScreenHandlerMixin extends ItemCombinerMenu {

    @Nullable
    @Shadow
    private RecipeHolder<SmithingRecipe> selectedRecipe;

    public SmithingScreenHandlerMixin(MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void updateResultMixin(CallbackInfo info, SmithingRecipeInput smithingRecipeInput, List<RecipeHolder<SmithingRecipe>> list) {
        if (!this.player.isCreative() && !((LevelManagerAccess) this.player).getLevelManager().hasRequiredCraftingLevel(list.get(0).value().assemble(smithingRecipeInput, this.player.level().registryAccess()).getItem())) {
            this.selectedRecipe = null;
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            info.cancel();
        }
    }
}
