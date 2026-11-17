package net.levelz.mixin.item;

import net.levelz.util.BonusHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {

    @Inject(method = "onConsume", at = @At("HEAD"))
    private void onConsumeMixin(Level level, LivingEntity user, ItemStack stack, Consumable consumable, CallbackInfo info) {
        if (user instanceof Player playerEntity) {
            BonusHelper.foodIncreasionBonus(playerEntity, stack);
        }
    }

}
