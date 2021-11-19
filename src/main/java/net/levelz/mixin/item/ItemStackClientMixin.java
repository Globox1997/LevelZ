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
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackClientMixin {

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getTooltipMixin(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info, List<Text> list, MutableText mutableText, int i) {
        if (player != null) {
            ItemStack stack = (ItemStack) (Object) this;
            ArrayList<Object> levelList = new ArrayList<Object>();

            // Block
            if (stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                // Alchemy check
                if (PlayerStatsManager.listContainsItemOrBlock(player, Registry.ITEM.getRawId(stack.getItem()), false)) {
                    list.add(new TranslatableText("item.levelz.alchemy.tooltip", PlayerStatsManager.getUnlockLevel(Registry.ITEM.getRawId(stack.getItem()), false)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
                if (block instanceof AnvilBlock) {
                    levelList = LevelLists.anvilList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.BEEHIVE) {
                    levelList = LevelLists.beehiveList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.BREWING_STAND) {
                    levelList = LevelLists.brewingStandList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.ENCHANTING_TABLE) {
                    levelList = LevelLists.enchantingTableList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.CAULDRON) {
                    levelList = LevelLists.cauldronList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.BARREL) {
                    levelList = LevelLists.barrelList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.BLAST_FURNACE) {
                    levelList = LevelLists.blastFurnaceList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.CARTOGRAPHY_TABLE) {
                    levelList = LevelLists.cartographyList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.COMPOSTER) {
                    levelList = LevelLists.composterList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.GRINDSTONE) {
                    levelList = LevelLists.grindstoneList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.LECTERN) {
                    levelList = LevelLists.lecternList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.LOOM) {
                    levelList = LevelLists.loomList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.SMITHING_TABLE) {
                    levelList = LevelLists.smithingTableList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.SMOKER) {
                    levelList = LevelLists.smokerList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (block == Blocks.STONECUTTER) {
                    levelList = LevelLists.stonecutterList;
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } else if (PlayerStatsManager.listContainsItemOrBlock(player, Registry.BLOCK.getRawId(block), true)) {
                    list.add(new TranslatableText("item.levelz.mining.tooltip", PlayerStatsManager.getUnlockLevel(Registry.BLOCK.getRawId(block), true)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else
            // Item
            if (stack.isIn(FabricToolTags.SHEARS)) {
                levelList = LevelLists.sheepList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.isIn(FabricToolTags.PICKAXES) || stack.isIn(FabricToolTags.SHOVELS)) {
                levelList = LevelLists.toolList;
                String material = ((ToolItem) stack.getItem()).getMaterial().toString().toLowerCase();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                            .formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.isIn(FabricToolTags.AXES)) {
                levelList = LevelLists.axeList;
                String material = ((ToolItem) stack.getItem()).getMaterial().toString().toLowerCase();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                            .formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.isIn(FabricToolTags.HOES)) {
                levelList = LevelLists.hoeList;
                String material = ((ToolItem) stack.getItem()).getMaterial().toString().toLowerCase();
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
                            .formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.isIn(FabricToolTags.SWORDS)) {
                levelList = LevelLists.swordList;
                String material = ((ToolItem) stack.getItem()).getMaterial().toString().toLowerCase();
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
            } else if (stack.getItem() == Items.GLASS_BOTTLE) {
                levelList = LevelLists.dragonBreathList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                levelList = LevelLists.totemList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
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
                try {
                    levelList = LevelLists.armorList;
                    String material = ((ArmorItem) stack.getItem()).getMaterial().getName().toLowerCase();
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
                        list.add(new TranslatableText("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip",
                                levelList.get(levelList.indexOf(material) + 2).toString()).formatted(Formatting.GRAY));
                        list.add(new TranslatableText("item.levelz.locked.tooltip"));
                    }
                } catch (AbstractMethodError ignored) {
                }
            } else if (stack.getItem() instanceof ShieldItem) {
                levelList = LevelLists.shieldList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() instanceof ElytraItem) {
                levelList = LevelLists.elytraList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() instanceof BucketItem) {
                levelList = LevelLists.bucketList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (stack.getItem() instanceof FishingRodItem) {
                levelList = LevelLists.fishingList;
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
                    list.add(new TranslatableText("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.GRAY));
                    list.add(new TranslatableText("item.levelz.locked.tooltip"));
                }
            } else if (PlayerStatsManager.listContainsItemOrBlock(player, Registry.ITEM.getRawId(stack.getItem()), false)) {
                list.add(new TranslatableText("item.levelz.alchemy.tooltip", PlayerStatsManager.getUnlockLevel(Registry.ITEM.getRawId(stack.getItem()), false)).formatted(Formatting.GRAY));
                list.add(new TranslatableText("item.levelz.locked.tooltip"));
            }
        }
    }

    @Shadow
    public Item getItem() {
        return null;
    }

}
