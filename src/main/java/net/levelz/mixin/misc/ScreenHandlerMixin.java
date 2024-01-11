package net.levelz.mixin.misc;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Shadow
    private ItemStack cursorStack;

    @Nullable
    private ScreenHandlerSyncHandler syncHandler;

    @Nullable
    @Shadow
    @Final
    private ScreenHandlerType<?> type;

    @Shadow
    @Final
    @Mutable
    public DefaultedList<Slot> slots = DefaultedList.of();

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canCombine(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", ordinal = 0), cancellable = true)
    private void internalOnSlotClickNewMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        if (8 - MobEntity.getPreferredEquipmentSlot(cursorStack).getEntitySlotId() == slotIndex
                && (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))) {
            if (cursorStack.getItem() instanceof ArmorItem armorItem) {
                ArrayList<Object> levelList = LevelLists.customItemList;
                try {
                    if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(armorItem).toString())) {
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(armorItem).toString(), true))
                            info.cancel();
                    } else {
                        levelList = LevelLists.armorList;
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
                            info.cancel();
                    }
                } catch (AbstractMethodError ignore) {
                }
            } else if (cursorStack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
                info.cancel();
            } else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(Registries.ITEM.getId(cursorStack.getItem()).toString())
                    && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(cursorStack.getItem()).toString(), true))
                info.cancel();
        } else if (type == ScreenHandlerType.BREWING_STAND && slotIndex == 3 && !cursorStack.isEmpty()) {
            if (PlayerStatsManager.listContainsItemOrBlock(player, Registries.ITEM.getRawId(cursorStack.getItem()), 2) && !player.isCreative()) {
                info.cancel();
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V", ordinal = 2, shift = Shift.BEFORE), cancellable = true)
    private void internalOnSlotClickMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        if (8 - MobEntity.getPreferredEquipmentSlot(cursorStack).getEntitySlotId() == slotIndex
                && (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))
                && this.slots.get(slotIndex).canInsert(cursorStack)) {
            if (cursorStack.getItem() instanceof ArmorItem armorItem) {
                ArrayList<Object> levelList = LevelLists.customItemList;
                try {
                    if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(armorItem).toString())) {
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(armorItem).toString(), true))
                            info.cancel();
                    } else {
                        levelList = LevelLists.armorList;
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
                            info.cancel();
                    }
                } catch (AbstractMethodError ignore) {
                }
            } else if (cursorStack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
                info.cancel();
            } else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(Registries.ITEM.getId(cursorStack.getItem()).toString())
                    && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(cursorStack.getItem()).toString(), true))
                info.cancel();
        } else if (type == ScreenHandlerType.BREWING_STAND && slotIndex == 3 && !cursorStack.isEmpty()) {
            // Slot 3: top; slot 0-2: bottom slots
            if (PlayerStatsManager.listContainsItemOrBlock(player, Registries.ITEM.getRawId(cursorStack.getItem()), 2) && !player.isCreative()) {
                info.cancel();
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V", ordinal = 1, shift = Shift.BEFORE), cancellable = true)
    private void internalOnSlotClickSwitchMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        if (8 - MobEntity.getPreferredEquipmentSlot(cursorStack).getEntitySlotId() == slotIndex
                && (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))) {
            if (cursorStack.getItem() instanceof ArmorItem armorItem) {
                ArrayList<Object> levelList = LevelLists.customItemList;
                try {
                    if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(armorItem).toString())) {
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(armorItem).toString(), true))
                            info.cancel();
                    } else {
                        levelList = LevelLists.armorList;
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
                            info.cancel();
                    }
                } catch (AbstractMethodError ignore) {
                }
            } else if (cursorStack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
                info.cancel();
            } else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(Registries.ITEM.getId(cursorStack.getItem()).toString())
                    && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(cursorStack.getItem()).toString(), true))
                info.cancel();
        } else if (type == ScreenHandlerType.BREWING_STAND && slotIndex == 3 && !cursorStack.isEmpty()) {
            if (PlayerStatsManager.listContainsItemOrBlock(player, Registries.ITEM.getRawId(cursorStack.getItem()), 2) && !player.isCreative()) {
                info.cancel();
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/screen/slot/Slot;getMaxItemCount(Lnet/minecraft/item/ItemStack;)I", ordinal = 2), cancellable = true)
    private void internalOnSlotSwitchSetMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemStack = playerInventory.getStack(button);
        if (8 - MobEntity.getPreferredEquipmentSlot(itemStack).getEntitySlotId() == slotIndex
                && (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))) {
            if (itemStack.getItem() instanceof ArmorItem armorItem) {
                ArrayList<Object> levelList = LevelLists.customItemList;
                try {
                    if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(armorItem).toString())) {
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(armorItem).toString(), true))
                            info.cancel();
                    } else {
                        levelList = LevelLists.armorList;
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
                            info.cancel();
                    }
                } catch (AbstractMethodError ignore) {
                }
            } else if (itemStack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
                info.cancel();
            } else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(Registries.ITEM.getId(itemStack.getItem()).toString())
                    && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(itemStack.getItem()).toString(), true))
                info.cancel();
        } else if (type == ScreenHandlerType.BREWING_STAND && slotIndex == 3 && !itemStack.isEmpty()) {
            if (PlayerStatsManager.listContainsItemOrBlock(player, Registries.ITEM.getRawId(itemStack.getItem()), 2) && !player.isCreative()) {
                info.cancel();
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/screen/slot/Slot;getMaxItemCount(Lnet/minecraft/item/ItemStack;)I", ordinal = 3), cancellable = true)
    private void internalOnSlotSwitchSwitchMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemStack = playerInventory.getStack(button);
        if (8 - MobEntity.getPreferredEquipmentSlot(itemStack).getEntitySlotId() == slotIndex
                && (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))) {
            if (itemStack.getItem() instanceof ArmorItem armorItem) {
                ArrayList<Object> levelList = LevelLists.customItemList;
                try {
                    if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(armorItem).toString())) {
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(armorItem).toString(), true))
                            info.cancel();
                    } else {
                        levelList = LevelLists.armorList;
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
                            info.cancel();
                    }
                } catch (AbstractMethodError ignore) {
                }
            } else if (itemStack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
                info.cancel();
            } else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(Registries.ITEM.getId(itemStack.getItem()).toString())
                    && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(itemStack.getItem()).toString(), true))
                info.cancel();
        } else if (type == ScreenHandlerType.BREWING_STAND && slotIndex == 3 && !itemStack.isEmpty()) {
            if (PlayerStatsManager.listContainsItemOrBlock(player, Registries.ITEM.getRawId(itemStack.getItem()), 2) && !player.isCreative()) {
                info.cancel();
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;", ordinal = 1), cancellable = true)
    private void internalOnSlotClickQuickMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        ItemStack itemStack = this.slots.get(slotIndex).getStack();
        if (itemStack.getItem() instanceof ArmorItem armorItem) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            try {
                if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(armorItem).toString())) {
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(armorItem).toString(), true))
                        info.cancel();
                } else {
                    levelList = LevelLists.armorList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
                        info.cancel();
                }
            } catch (AbstractMethodError ignore) {
            }
        } else if (itemStack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
            info.cancel();
        } else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(Registries.ITEM.getId(itemStack.getItem()).toString())
                && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registries.ITEM.getId(itemStack.getItem()).toString(), true))
            info.cancel();
    }
}
