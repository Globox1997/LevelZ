package net.levelz.mixin.item;

import net.levelz.util.BonusHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PotionContents.class)
public class PotionItemMixin {

    @ModifyVariable(method = "lambda$applyToLivingEntity$0", at = @At("HEAD"), argsOnly = true)
    private static MobEffectInstance finishUsingMixin(MobEffectInstance original, ServerLevel serverLevel, Player playerEntity, LivingEntity livingEntity, MobEffectInstance statusEffectInstance) {
        return BonusHelper.potionEffectChanceBonus(playerEntity, original);
    }

}
