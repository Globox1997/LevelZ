package net.levelz.mixin.compat;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import amorphia.alloygery.content.machines.recipe.SmithingAnvilRecipe;
import amorphia.alloygery.content.machines.screen.SmithingAnvilScreenHandler;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

@Mixin(SmithingAnvilScreenHandler.class)
public abstract class SmithingAnvilScreenHandlerMixin extends ScreenHandler {

    @Shadow
    @Mutable
    @Final
    Slot outputSlot;

    @Nullable
    private PlayerEntity playerEntity = null;

    public SmithingAnvilScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "Lamorphia/alloygery/content/machines/screen/SmithingAnvilScreenHandler;<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
    private void initMixin(int syncId, PlayerInventory playerInventory, Inventory blockInventory, ScreenHandlerContext context, CallbackInfo info) {
        this.playerEntity = playerInventory.player;
    }

    // @Inject(method = "updateInput", at = @At(value = "INVOKE", target = "Lamorphia/alloygery/content/machines/screen/SmithingAnvilScreenHandler;sendContentUpdates()V"), cancellable = true)
    // void updateInputMixin(Inventory inventory, CallbackInfo info) {//
    // if (!this.availableRecipes.isEmpty() && this.playerEntity != null && !this.playerEntity.isCreative()) {
    // Iterator<SmithingAnvilRecipe> iterator = this.availableRecipes.iterator();
    // while (iterator.hasNext()) {
    // SmithingAnvilRecipe recipe = iterator.next();
    // if (PlayerStatsManager.listContainsItemOrBlock(this.playerEntity, Registry.ITEM.getRawId(recipe.getOutput().getItem()), 3)) {
    // this.availableRecipes.remove(recipe);
    // }
    // }
    // }
    // }

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setLastRecipe(Lnet/minecraft/recipe/Recipe;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    void updateResultMixin(CallbackInfo info, SmithingAnvilRecipe recipe) {
        if (this.playerEntity != null && !this.playerEntity.isCreative()
                && PlayerStatsManager.listContainsItemOrBlock(this.playerEntity, Registries.ITEM.getRawId(recipe.getOutput(playerEntity.getWorld().getRegistryManager()).getItem()), 3)) {
            this.outputSlot.setStack(ItemStack.EMPTY);
            this.sendContentUpdates();
            info.cancel();
        }
    }

}
