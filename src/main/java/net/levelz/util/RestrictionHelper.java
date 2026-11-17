package net.levelz.util;

import io.wispforest.accessories.menu.variants.AccessoriesMenuBase;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RestrictionHelper {

    private static final boolean isAccessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");

    public static boolean restrictSlotClick(Player playerEntity, ClickType actionType, ItemStack cursorStack, Slot slot, AbstractContainerMenu screenHandler) {
        if (!playerEntity.isCreative()) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (actionType.equals(ClickType.QUICK_MOVE)) {
                return !slot.getItem().isEmpty() && !levelManager.hasRequiredStackLevel(slot.getItem());
            } else if (!cursorStack.isEmpty()) {
                boolean isNonNormalSlot = !slot.getClass().equals(Slot.class);
                if (!levelManager.hasRequiredStackLevel(cursorStack)) {
                    if (screenHandler instanceof InventoryMenu) {
                        if (isNonNormalSlot) {
                            return true;
                        }
                    } else if (isAccessoriesLoaded && screenHandler instanceof AccessoriesMenuBase) {
                        if (isNonNormalSlot) {
                            return true;
                        }
                    }
                }
                if (!levelManager.hasRequiredCraftingLevel(cursorStack.getItem()) && isNonNormalSlot) {
                    return true;
                }
            }
        }

        return false;
    }
}
