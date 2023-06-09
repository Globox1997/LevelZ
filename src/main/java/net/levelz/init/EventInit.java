package net.levelz.init;

import java.util.ArrayList;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.data.LevelLists;
import net.levelz.mixin.entity.EntityAccessor;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class EventInit {

    public static void init() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((PlayerSyncAccess) player).syncStats(false);
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                ArrayList<Object> customList = LevelLists.customItemList;
                String string = Registries.ITEM.getId(player.getStackInHand(hand).getItem()).toString();
                if (!customList.isEmpty() && !PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
                    player.sendMessage(
                            Text.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2)).formatted(Formatting.RED),
                            true);
                    return TypedActionResult.fail(player.getStackInHand(hand));
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        UseBlockCallback.EVENT.register((player, world, hand, result) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
                if (world.canPlayerModifyAt(player, blockPos)) {
                    String string = Registries.BLOCK.getId(world.getBlockState(blockPos).getBlock()).toString();
                    ArrayList<Object> customList = LevelLists.customBlockList;
                    if (!customList.isEmpty() && customList.contains(string)) {
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
                            player.sendMessage(Text.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2))
                                    .formatted(Formatting.RED), true);
                            return ActionResult.success(false);
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                if (!entity.hasControllingPassenger() || !((EntityAccessor) entity).callCanAddPassenger(player)) {
                    String string = Registries.ENTITY_TYPE.getId(entity.getType()).toString();
                    ArrayList<Object> customList = LevelLists.customEntityList;
                    if (!customList.isEmpty() && customList.contains(string)) {
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
                            player.sendMessage(Text.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2))
                                    .formatted(Formatting.RED), true);
                            return ActionResult.success(false);
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

}
