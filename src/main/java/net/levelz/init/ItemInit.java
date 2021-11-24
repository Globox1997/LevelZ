package net.levelz.init;

import java.util.ArrayList;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.levelz.data.LevelLists;
import net.levelz.item.*;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class ItemInit {

    public static final Item STRANGE_POTION = new StrangePotionItem(new Item.Settings().group(ItemGroup.BREWING).maxCount(1));

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier("levelz", "strange_potion"), STRANGE_POTION);

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                ArrayList<Object> customList = LevelLists.customItemList;
                String string = Registry.ITEM.getId(player.getStackInHand(hand).getItem()).toString();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
                    player.sendMessage(new TranslatableText("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2)), true);
                    return TypedActionResult.fail(player.getStackInHand(hand));
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        UseBlockCallback.EVENT.register((player, world, hand, result) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
                if (world.canPlayerModifyAt(player, blockPos)) {
                    String string = Registry.BLOCK.getId(world.getBlockState(blockPos).getBlock()).toString();
                    ArrayList<Object> customList = LevelLists.customBlockList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
                        player.sendMessage(new TranslatableText("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2)), true);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                String string = Registry.ENTITY_TYPE.getId(entity.getType()).toString();
                ArrayList<Object> customList = LevelLists.customEntityList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
                    player.sendMessage(new TranslatableText("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2))
                            .formatted(Formatting.RED), true);
                    return ActionResult.FAIL;
                }

            }
            return ActionResult.PASS;
        });
    }
}
