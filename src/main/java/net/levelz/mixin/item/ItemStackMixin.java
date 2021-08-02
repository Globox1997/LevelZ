package net.levelz.mixin.item;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.fabricmc.api.EnvType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackMixin {

    // private void addTooltip(PlayerEntity playerEntity, List<Object> levelList, List<Text> list, boolean material) {
    // if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, null, false)) {
    // if(material){

    // }else{
    // list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
    // }
    // list.add(new TranslatableText("item.levelz.locked.tooltip"));
    // }
    // }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getTooltipMixin(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info, List<Text> list, int i) {
        if (player != null) {
            ItemStack stack = (ItemStack) (Object) this;
            ArrayList<Object> levelList = new ArrayList<Object>();

            if (stack.isIn(FabricToolTags.SHEARS)) {
                levelList = LevelLists.sheepList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.isIn(FabricToolTags.AXES) || stack.isIn(FabricToolTags.PICKAXES) || stack.isIn(FabricToolTags.SHOVELS)) {
                levelList = LevelLists.toolList;
                String material = ((MiningToolItem) stack.getItem()).getMaterial().toString().toLowerCase();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                            .formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.isIn(FabricToolTags.HOES)) {
                levelList = LevelLists.hoeList;
                String material = ((MiningToolItem) stack.getItem()).getMaterial().toString().toLowerCase();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                            .formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() == Items.FLINT_AND_STEEL) {
                levelList = LevelLists.flintAndSteelList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.isIn(FabricToolTags.SWORDS)) {
                levelList = LevelLists.swordList;
                String material = ((SwordItem) stack.getItem()).getMaterial().toString().toLowerCase();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                            .formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() instanceof BowItem) {
                levelList = LevelLists.bowList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() instanceof TridentItem) {
                levelList = LevelLists.tridentList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() instanceof CrossbowItem) {
                levelList = LevelLists.crossbowList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() instanceof ArmorItem) {
                levelList = LevelLists.armorList;
                String material = ((ArmorItem) stack.getItem()).getMaterial().getName();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                            .formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() instanceof ShieldItem) {
                levelList = LevelLists.shieldList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() == Items.ELYTRA) {
                levelList = LevelLists.elytraList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            }
        }
    }

    @Shadow
    public Item getItem() {
        return null;
    }

}
