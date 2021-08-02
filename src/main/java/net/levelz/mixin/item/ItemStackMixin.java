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
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
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

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getTooltipMixin(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info, List<Text> list, int i) {
        if (player != null) {
            ItemStack stack = (ItemStack) (Object) this;
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
            ArrayList<Object> levelList = new ArrayList<Object>();

            if (stack.isIn(FabricToolTags.SHEARS) && playerStatsManager.getLevel("farming") < 5) {
                list.add(new TranslatableText("item.levelz.farming.tooltip", 5).formatted(Formatting.GRAY));
                list.add(new TranslatableText("item.levelz.locked.tooltip"));
            } else if (stack.getItem() == Items.WOODEN_HOE && playerStatsManager.getLevel("farming") < 1) {
                list.add(new TranslatableText("item.levelz.farming.tooltip", 1).formatted(Formatting.GRAY));
                list.add(new TranslatableText("item.levelz.locked.tooltip"));
            } else if (stack.isIn(FabricToolTags.AXES) || stack.isIn(FabricToolTags.PICKAXES) || stack.isIn(FabricToolTags.SHOVELS)) {
                levelList = LevelLists.toolList;
                String material = ((MiningToolItem) stack.getItem()).getMaterial().toString().toLowerCase();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz.mining.tooltip", levelList.get(levelList.indexOf(material) + 2).toString()).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.isIn(FabricToolTags.HOES)) {
                levelList = LevelLists.hoeList;
                String material = ((MiningToolItem) stack.getItem()).getMaterial().toString().toLowerCase();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz.farming.tooltip", levelList.get(levelList.indexOf(material) + 2).toString()).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() == Items.FLINT_AND_STEEL && playerStatsManager.getLevel("farming") < 8) {
                list.add(new TranslatableText("item.levelz.farming.tooltip", 8).formatted(Formatting.GRAY));
                list.add(new TranslatableText("item.levelz.locked.tooltip"));
            } else if (stack.isIn(FabricToolTags.SWORDS)) {
                int playerStrengthLevel = playerStatsManager.getLevel("strength");
                if (playerStrengthLevel < ConfigInit.CONFIG.maxLevel) {
                    int itemMaterialLevel = ((SwordItem) stack.getItem()).getMaterial().getMiningLevel() * 4;
                    if (itemMaterialLevel > playerStrengthLevel) {
                        list.add(new TranslatableText("item.levelz.strength.tooltip", itemMaterialLevel).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                }
            } else if (stack.getItem() instanceof BowItem && playerStatsManager.getLevel("archery") < 1) {
                list.add(new TranslatableText("item.levelz.archery.tooltip", 1).formatted(Formatting.GRAY));
                list.add(new TranslatableText("item.levelz.locked.tooltip"));
            } else if (stack.getItem() instanceof TridentItem && playerStatsManager.getLevel("archery") < 16) {
                list.add(new TranslatableText("item.levelz.archery.tooltip", 16).formatted(Formatting.GRAY));
                list.add(new TranslatableText("item.levelz.locked.tooltip"));
            } else if (stack.getItem() instanceof CrossbowItem && playerStatsManager.getLevel("archery") < 8) {
                list.add(new TranslatableText("item.levelz.archery.tooltip", 8).formatted(Formatting.GRAY));
                list.add(new TranslatableText("item.levelz.locked.tooltip"));
            } else if (stack.getItem() instanceof ArmorItem) {

                levelList = LevelLists.armorList;
                String material = ((ArmorItem) stack.getItem()).getMaterial().getName();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz.defense.tooltip", levelList.get(levelList.indexOf(material) + 2).toString()).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
                // int playerDefenseLevel = playerStatsManager.getLevel("defense");
                // if (playerDefenseLevel < ConfigInit.CONFIG.maxLevel) {
                // int itemMaterialLevel = (int) (((ArmorItem) stack.getItem()).getMaterial().getEnchantability() / 2.5F);
                // if (itemMaterialLevel > playerDefenseLevel) {
                // list.add(new TranslatableText("item.levelz.defense.tooltip", itemMaterialLevel).formatted(Formatting.GRAY));
                // list.add(new TranslatableText("item.levelz.locked.tooltip"));
                // }
                // }
            } else if (stack.getItem() instanceof ShieldItem && playerStatsManager.getLevel("defense") < 5) {
                list.add(new TranslatableText("item.levelz.defense.tooltip", 5).formatted(Formatting.GRAY));
                list.add(new TranslatableText("item.levelz.locked.tooltip"));
            } else if (stack.getItem() == Items.ELYTRA && playerStatsManager.getLevel("agility") < 10) {
                list.add(new TranslatableText("item.levelz.agility.tooltip", 10).formatted(Formatting.GRAY));
                list.add(new TranslatableText("item.levelz.locked.tooltip"));
            }
        }
    }

    @Shadow
    public Item getItem() {
        return null;
    }

}
