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
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

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
        if (trinketInventory.getComponent().getEntity() instanceof PlayerEntity
                && TrinketsApi.getTrinket(stack.getItem()).canEquip(stack, new SlotReference(trinketInventory, slotOffset), trinketInventory.getComponent().getEntity())) {
            ArrayList<Object> customList = LevelLists.customItemList;
            String string = Registry.ITEM.getId(stack.getItem()).toString();
            if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) trinketInventory.getComponent().getEntity(), customList, string, true)) {
                ((PlayerEntity) trinketInventory.getComponent().getEntity()).sendMessage(
                        Text.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2)).formatted(Formatting.RED),
                        true);
                info.setReturnValue(false);
            }
        }
    }

}
