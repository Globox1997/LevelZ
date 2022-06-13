package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {

    @Inject(method = "postMine", at = @At("HEAD"), cancellable = true)
    private void postMineMixin(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> info) {
        if (miner instanceof PlayerEntity) {
            if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) miner, LevelLists.shearsList, null, true))
                info.setReturnValue(false);
        }
    }

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemUsageContext;getStack()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        ArrayList<Object> levelList = LevelLists.shearsList;
        if (!PlayerStatsManager.playerLevelisHighEnough(context.getPlayer(), levelList, null, true)) {
            context.getPlayer().sendMessage(Text.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.RED), true);
            info.setReturnValue(ActionResult.FAIL);
        }
    }
}
