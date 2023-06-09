package net.levelz.mixin.compat;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.trinkets.SurvivalTrinketSlot;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

@Mixin(SurvivalTrinketSlot.class)
public class SurvivalTrinketSlotMixin {

    @Shadow
    @Mutable
    @Final
    private TrinketInventory trinketInventory;

    @Shadow
    @Mutable
    @Final
    private int slotOffset;

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void canInsertMixin(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (trinketInventory.getComponent().getEntity() instanceof PlayerEntity player
                && TrinketsApi.getTrinket(stack.getItem()).canEquip(stack, new SlotReference(trinketInventory, slotOffset), trinketInventory.getComponent().getEntity())) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(stack.getItem()).toString())) {
                String string = Registries.ITEM.getId(stack.getItem()).toString();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, string, true)) {
                    info.setReturnValue(false);
                }
            } else if (stack.getItem() instanceof ArmorItem armorItem) {
                levelList = LevelLists.armorList;
                String string = armorItem.getMaterial().getName().toLowerCase();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, string, true)) {
                    info.setReturnValue(false);
                }
            } else {
                levelList = LevelLists.elytraList;
                if (stack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
                    info.setReturnValue(false);
                }
            }
        }
    }

}
