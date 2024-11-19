package net.levelz.mixin.misc;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    @Inject(method = "dropBarteredItem(Lnet/minecraft/entity/mob/PiglinEntity;Lnet/minecraft/entity/player/PlayerEntity;Ljava/util/List;)V", at = @At("HEAD"), cancellable = true)
    private static void dropBarteredItemMixin(PiglinEntity piglin, PlayerEntity player, List<ItemStack> items, CallbackInfo info) {
        if (player.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
        if (!levelManager.hasRequiredEntityLevel(piglin.getType())) {
            player.sendMessage(Text.translatable("item.levelz.locked.tooltip").formatted(Formatting.RED), true);
            if (!items.isEmpty()) {
                piglin.swingHand(Hand.OFF_HAND);
                LookTargetUtil.give(piglin, new ItemStack(Items.GOLD_INGOT), player.getPos().add(0.0, 1.0, 0.0));
            }
            info.cancel();
        }
    }

}
