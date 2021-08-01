package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
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
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        int playerMiningLevel = playerStatsManager.getLevel("mining");
        if (playerMiningLevel < ConfigInit.CONFIG.maxLevel) {
            ItemStack itemStack = this.main.get(this.selectedSlot);
            if (itemStack.getItem() instanceof MiningToolItem && ((MiningToolItem) itemStack.getItem()).getMaterial().getMiningLevel() * 4 > playerMiningLevel) {
                info.setReturnValue(1.0F);
            }
        }
    }

    // Wood, Stone, Iron,Gold, Diamond, Netherite
    // return ((ItemStack) this.main.get(this.selectedSlot)).getMiningSpeedMultiplier(block);

}
