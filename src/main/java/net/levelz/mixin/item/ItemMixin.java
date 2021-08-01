package net.levelz.mixin.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.FarmlandBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.screen.PlayerScreenHandler;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
@Mixin(Item.class)
public class ItemMixin {

    // private static final Tag<Item> COMMON_ITEM = TagRegistry.item(new Identifier("tooltiprareness", "common_item"));
    // private static final Tag<Item> UNCOMMON_ITEM = TagRegistry.item(new Identifier("tooltiprareness", "uncommon_item"));
    // private static final Tag<Item> RARE_ITEM = TagRegistry.item(new Identifier("tooltiprareness", "rare_item"));
    // private static final Tag<Item> EPIC_ITEM = TagRegistry.item(new Identifier("tooltiprareness", "epic_item"));
    // private static final Tag<Item> LEGENDARY_ITEM = TagRegistry
    // .item(new Identifier("tooltiprareness", "legendary_item"));
    // private static final Tag<Item> ADMIN_ITEM = TagRegistry.item(new Identifier("tooltiprareness", "admin_item"));

    // @Inject(method = "appendTooltip", at = @At("HEAD"))
    // private void appendTooltipMixin(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo info) {
    // if (stack.getItem().equals(Items.SHEARS)) {
    // tooltip.add(new TranslatableText("item.levelz.farming.tooltip", 5).formatted(Formatting.GRAY));
    // tooltip.add(new TranslatableText("item.levelz.locked.tooltip"));
    // }
    // }

}
