package net.levelz.mixin.misc;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
    public DefaultedList<Slot> slots = DefaultedList.of();

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V", ordinal = 2, shift = Shift.BEFORE), cancellable = true)
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        if (8 - MobEntity.getPreferredEquipmentSlot(cursorStack).getEntitySlotId() == slotIndex && this.slots.get(slotIndex).toString().contains("PlayerScreenHandler")
                && this.slots.get(slotIndex).canInsert(cursorStack)) {
            if (!player.isCreative()) {
                PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
                if (cursorStack.getItem() instanceof ArmorItem) {
                    int playerDefenseLevel = playerStatsManager.getLevel("defense");
                    if (playerDefenseLevel < ConfigInit.CONFIG.maxLevel) {
                        int itemMaterialLevel = (int) (((ArmorItem) cursorStack.getItem()).getMaterial().getEnchantability() / 2.5F);
                        if (itemMaterialLevel > playerDefenseLevel) {
                            info.cancel();
                        }
                    }
                } else if (cursorStack.getItem() == Items.ELYTRA) {
                    int playerAgilityLevel = playerStatsManager.getLevel("agility");
                    if (playerAgilityLevel < ConfigInit.CONFIG.maxLevel) {
                        if (10 > playerAgilityLevel) {
                            info.cancel();
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;", ordinal = 1), cancellable = true)
    private void internalOnSlotClickQuickMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        if (!player.isCreative()) {
            ItemStack itemStack = this.slots.get(slotIndex).getStack();
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
            if (itemStack.getItem() instanceof ArmorItem) {
                int playerDefenseLevel = playerStatsManager.getLevel("defense");
                if (playerDefenseLevel < ConfigInit.CONFIG.maxLevel) {
                    int itemMaterialLevel = (int) (((ArmorItem) itemStack.getItem()).getMaterial().getEnchantability() / 2.5F);
                    if (itemMaterialLevel > playerDefenseLevel) {
                        info.cancel();
                    }
                }
            } else if (cursorStack.getItem() == Items.ELYTRA && PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList)) {
                info.cancel();
            }
        }
    }
}
