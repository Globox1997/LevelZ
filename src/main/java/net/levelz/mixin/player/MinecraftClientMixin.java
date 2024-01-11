package net.levelz.mixin.player;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
@Mixin(value = MinecraftClient.class, priority = 999)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreakingMixin(boolean breaking, CallbackInfo info) {
        if (restrictHandUsage(true)) {
            info.cancel();
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void doAttackMixin(CallbackInfoReturnable<Boolean> info) {
        if (restrictHandUsage(false)) {
            info.setReturnValue(false);
        }
    }

    private boolean restrictHandUsage(boolean blockBreaking) {
        if (ConfigInit.CONFIG.lockedHandUsage && player != null && !player.isCreative()) {
            Item item = player.getMainHandStack().getItem();
            if (item != null && !item.equals(Items.AIR)) {
                ArrayList<Object> levelList = LevelLists.customItemList;
                if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(item).toString())) {
                    String string = Registries.ITEM.getId(item).toString();
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, string, true)) {
                        player.sendMessage(
                                Text.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).formatted(Formatting.RED),
                                true);
                        return true;
                    }
                } else if (item instanceof ToolItem) {
                    levelList = null;
                    if (item instanceof SwordItem) {
                        levelList = LevelLists.swordList;
                    } else if (item instanceof AxeItem) {
                        if (ConfigInit.CONFIG.bindAxeDamageToSwordRestriction && !blockBreaking) {
                            levelList = LevelLists.swordList;
                        } else {
                            levelList = LevelLists.axeList;
                        }
                    } else if (item instanceof HoeItem) {
                        levelList = LevelLists.hoeList;
                    } else if (item instanceof PickaxeItem || item instanceof ShovelItem) {
                        levelList = LevelLists.toolList;
                    }
                    if (levelList != null && ((ToolItem) item).getMaterial() != null) {
                        String material = ((ToolItem) item).getMaterial().toString().toLowerCase();
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, true)) {
                            player.sendMessage(Text
                                    .translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                                    .formatted(Formatting.RED), true);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
