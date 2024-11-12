package net.levelz.util;

import io.wispforest.accessories.menu.variants.AccessoriesMenuBase;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class RestrictionHelper {

    private static final boolean isAccessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");

    public static boolean restrictSlotClick(PlayerEntity playerEntity, SlotActionType actionType, ItemStack cursorStack, Slot slot, ScreenHandler screenHandler) {
        if (!playerEntity.isCreative()) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (actionType.equals(SlotActionType.QUICK_MOVE)) {
                return !slot.getStack().isEmpty() && !levelManager.hasRequiredItemLevel(slot.getStack().getItem());
            } else if (!cursorStack.isEmpty()) {
                boolean isNonNormalSlot = !slot.getClass().equals(Slot.class);
                if (!levelManager.hasRequiredItemLevel(cursorStack.getItem())) {
                    if (screenHandler instanceof PlayerScreenHandler) {
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
