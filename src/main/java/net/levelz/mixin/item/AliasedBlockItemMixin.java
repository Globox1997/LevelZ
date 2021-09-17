package net.levelz.mixin.item;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;

@Mixin(AliasedBlockItem.class)
public abstract class AliasedBlockItemMixin extends BlockItem {
    public AliasedBlockItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    // @Override
    // public ActionResult place(ItemPlacementContext context) {
    // return super.place(context);
    // }
}
