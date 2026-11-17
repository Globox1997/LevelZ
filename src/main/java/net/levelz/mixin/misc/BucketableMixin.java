package net.levelz.mixin.misc;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SuppressWarnings("rawtypes")
@Mixin(Bucketable.class)
public interface BucketableMixin {

    @Inject(method = "bucketMobPickup(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), cancellable = true)
    private static <T extends LivingEntity> void tryBucketMixin(Player player, InteractionHand hand, T entity, CallbackInfoReturnable<Optional> info) {
        if (player.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
        if (!levelManager.hasRequiredStackLevel(player.getItemInHand(hand))) {
            player.displayClientMessage(Component.translatable("item.levelz.locked.tooltip").withStyle(ChatFormatting.RED), true);
            info.setReturnValue(Optional.empty());
        }
    }
}
