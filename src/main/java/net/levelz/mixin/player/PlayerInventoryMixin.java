package net.levelz.mixin.player;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.util.collection.DefaultedList;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Shadow
    @Final
    public PlayerEntity player;

    @Shadow
    @Final
    public DefaultedList<ItemStack> main;

    @Shadow
    public int selectedSlot;

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "HEAD"), cancellable = true)
    private void getBlockBreakingSpeedMixin(BlockState block, CallbackInfoReturnable<Float> info) {
        ItemStack itemStack = this.main.get(this.selectedSlot);
        if (itemStack.getItem() instanceof MiningToolItem) {
            ArrayList<Object> itemList;
            if (itemStack.isIn(FabricToolTags.HOES)) {
                itemList = LevelLists.hoeList;
            } else if (itemStack.isIn(FabricToolTags.AXES)) {
                itemList = LevelLists.axeList;
            } else {
                itemList = LevelLists.toolList;
            }
            if (!PlayerStatsManager.playerLevelisHighEnough(player, itemList, ((MiningToolItem) this.main.get(this.selectedSlot).getItem()).getMaterial().toString().toLowerCase(), true)) {
                info.setReturnValue(1.0F);
            }
        }
    }

}
