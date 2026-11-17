package net.levelz.mixin.misc;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PiglinAi.class)
public class PiglinBrainMixin {

    @Inject(method = "throwItemsTowardPlayer(Lnet/minecraft/world/entity/monster/piglin/Piglin;Lnet/minecraft/world/entity/player/Player;Ljava/util/List;)V", at = @At("HEAD"), cancellable = true)
    private static void dropBarteredItemMixin(Piglin piglin, Player player, List<ItemStack> items, CallbackInfo info) {
        if (player.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
        if (!levelManager.hasRequiredEntityLevel(piglin.getType())) {
            player.displayClientMessage(Component.translatable("item.levelz.locked.tooltip").withStyle(ChatFormatting.RED), true);
            if (!items.isEmpty()) {
                piglin.swing(InteractionHand.OFF_HAND);
                BehaviorUtils.throwItem(piglin, new ItemStack(Items.GOLD_INGOT), player.position().add(0.0, 1.0, 0.0));
            }
            info.cancel();
        }
    }

}
