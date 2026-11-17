package net.levelz.mixin.entity;

import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.util.BonusHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Villager.class)
public abstract class VillagerEntityMixin extends AbstractVillager {

    @Shadow
    @Nullable
    private Player lastTradedPlayer;

    public VillagerEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }


    @Inject(method = "rewardTradeXp", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void afterUsingMixin(MerchantOffer offer, CallbackInfo info, int i) {
        BonusHelper.tradeXpBonus((ServerLevel) this.level(), this.lastTradedPlayer, this, i);
    }

    @Inject(method = "updateSpecialPrices", at = @At(value = "TAIL"))
    private void prepareOffersForMixin(Player player, CallbackInfo info) {
        if (!player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            for (MerchantOffer tradeOffer : this.getOffers()) {
                int originalPrice = tradeOffer.getBaseCostA().getCount();
                tradeOffer.addToSpecialPriceDiff(-(int) (originalPrice - originalPrice * BonusHelper.priceDiscountBonus(player)));
            }
        }
    }

    @Inject(method = "setLastHurtByMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;onReputationEvent(Lnet/minecraft/world/entity/ai/village/ReputationEventType;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/ReputationEventHandler;)V"), cancellable = true)
    private void setAttackerMixin(@Nullable LivingEntity attacker, CallbackInfo info) {
        if (attacker instanceof Player playerEntity && BonusHelper.merchantImmuneBonus(playerEntity)) {
            super.setLastHurtByMob(attacker);
            info.cancel();
        }
    }

}