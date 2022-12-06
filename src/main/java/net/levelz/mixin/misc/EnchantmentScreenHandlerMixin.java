package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;

import java.util.ArrayList;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {

    @Shadow
    @Final
    private Inventory inventory;
    @Shadow
    @Final
    public int[] enchantmentPower;
    @Shadow
    @Final
    public int[] enchantmentId;
    @Shadow
    @Final
    public int[] enchantmentLevel;

    private PlayerInventory playerInventory;

    @Inject(method = "Lnet/minecraft/screen/EnchantmentScreenHandler;<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "TAIL"))
    private void initMixin(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo info) {
        this.playerInventory = playerInventory;
    }

    @Inject(method = "onContentChanged", at = @At(value = "TAIL"))
    private void onContentChangedMixin(Inventory inventory, CallbackInfo info) {
        if (inventory == this.inventory && playerInventory != null && !playerInventory.player.isCreative()) {
            ItemStack itemStack = inventory.getStack(0);
            if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
                PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerInventory.player).getPlayerStatsManager();
                ArrayList<Object> enchantingTableList = LevelLists.enchantingTableList;
                if (enchantingTableList != null && !enchantingTableList.isEmpty()) {
                    int playerAlchemyLevel = playerStatsManager.getSkillLevel(Skill.valueOf(enchantingTableList.get(0).toString().toUpperCase()));
                    if (playerAlchemyLevel < ConfigInit.CONFIG.maxLevel) {
                        if (playerAlchemyLevel < (int) enchantingTableList.get(4)) {
                            for (int i = 0; i < 3; ++i) {
                                this.enchantmentPower[i] = 0;
                                this.enchantmentId[i] = -1;
                                this.enchantmentLevel[i] = -1;
                            }
                        } else if (playerAlchemyLevel < (int) enchantingTableList.get(5)) {
                            for (int i = 1; i < 3; ++i) {
                                this.enchantmentPower[i] = 0;
                                this.enchantmentId[i] = -1;
                                this.enchantmentLevel[i] = -1;
                            }
                        } else if (playerAlchemyLevel < (int) enchantingTableList.get(6)) {
                            this.enchantmentPower[2] = 0;
                            this.enchantmentId[2] = -1;
                            this.enchantmentLevel[2] = -1;
                        }
                    }
                }
            }
        }
    }
}
