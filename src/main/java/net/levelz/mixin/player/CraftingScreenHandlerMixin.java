package net.levelz.mixin.player;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CraftingMenu.class)
public class CraftingScreenHandlerMixin {

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void updateResultMixin(AbstractContainerMenu handler, Level world, Player player, CraftingContainer craftingInventory, ResultContainer resultInventory, @Nullable RecipeHolder<CraftingRecipe> recipe, CallbackInfo info, CraftingInput craftingRecipeInput, ServerPlayer serverPlayerEntity, ItemStack itemStack) {
        if (player.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
        if (!levelManager.hasRequiredCraftingLevel(itemStack.getItem())) {
            resultInventory.setItem(0, ItemStack.EMPTY);
            handler.setRemoteSlot(0, ItemStack.EMPTY);
            serverPlayerEntity.connection.send(new ClientboundContainerSetSlotPacket(handler.containerId, handler.incrementStateId(), 0, ItemStack.EMPTY));
            info.cancel();
        }
    }

}
