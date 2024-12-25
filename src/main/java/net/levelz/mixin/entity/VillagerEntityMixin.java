package net.levelz.mixin.entity;

import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.util.BonusHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    @Shadow
    @Nullable
    private PlayerEntity lastCustomer;

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "afterUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void afterUsingMixin(TradeOffer offer, CallbackInfo info, int i) {
        BonusHelper.tradeXpBonus((ServerWorld) this.getWorld(), this.lastCustomer, this, i);
    }

    @Inject(method = "prepareOffersFor", at = @At(value = "TAIL"))
    private void prepareOffersForMixin(PlayerEntity player, CallbackInfo info) {
        if (!player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                int originalPrice = tradeOffer.getOriginalFirstBuyItem().getCount();
                tradeOffer.increaseSpecialPrice(-(int) (originalPrice - originalPrice * BonusHelper.priceDiscountBonus(player)));
            }
        }
    }

    @Inject(method = "setAttacker", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;handleInteraction(Lnet/minecraft/entity/EntityInteraction;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/InteractionObserver;)V"), cancellable = true)
    private void setAttackerMixin(@Nullable LivingEntity attacker, CallbackInfo info) {
        if (attacker instanceof PlayerEntity playerEntity && BonusHelper.merchantImmuneBonus(playerEntity)) {
            super.setAttacker(attacker);
            info.cancel();
        }
    }

}