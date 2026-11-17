package net.levelz.mixin.entity;

import net.levelz.access.LevelManagerAccess;
import net.levelz.access.MobEntityAccess;
import net.levelz.access.PlayerDropAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.TagInit;
import net.levelz.level.LevelManager;
import net.levelz.util.BonusHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected int lastHurtByPlayerTime;

    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyVariable(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageProtection(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;)F", shift = At.Shift.AFTER), ordinal = 1)
    private float modifyAppliedDamageMixin(float original, DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) && (Object) this instanceof Player playerEntity) {
            return original + BonusHelper.fallDamageReductionBonus(playerEntity);
        } else {
            return original;
        }
    }

    @ModifyVariable(method = "checkTotemDeathProtection", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    private ItemStack tryUseTotemMixin(ItemStack original) {
        if ((Object) this instanceof Player playerEntity && original.is(Items.TOTEM_OF_UNDYING)) {
            if (playerEntity.isCreative()) {
                return original;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (!levelManager.hasRequiredStackLevel(original)) {
                return ItemStack.EMPTY;
            }
        }
        return original;
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/InteractionHand;values()[Lnet/minecraft/world/InteractionHand;"), cancellable = true)
    private void tryUseTotemMixin(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        if ((Object) this instanceof Player playerEntity && BonusHelper.deathGraceChanceBonus(playerEntity)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At(value = "HEAD"), cancellable = true)
    protected void dropMixin(ServerLevel world, DamageSource damageSource, CallbackInfo info) {
        if (!((Object) this instanceof Player) && lastHurtByPlayer != null && this.lastHurtByPlayerTime > 0 && ConfigInit.CONFIG.disableMobFarms
                && !((PlayerDropAccess) lastHurtByPlayer).allowMobDrop()) {
            info.cancel();
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void onDeathMixin(DamageSource source, CallbackInfo info) {
        if (lastHurtByPlayer != null && this.lastHurtByPlayerTime > 0 && ConfigInit.CONFIG.disableMobFarms) {
            ((PlayerDropAccess) lastHurtByPlayer).increaseKilledMobStat(this.level().getChunk(this.blockPosition()));
        }
    }

    @Inject(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    protected void dropXpMixin(CallbackInfo info) {
        if (ConfigInit.CONFIG.mobXPMultiplier > 0.0F) {
            if ((!ConfigInit.CONFIG.spawnerMobXP && (Object) this instanceof Mob mobEntity && ((MobEntityAccess) mobEntity).isSpawnerMob()) || ((Object) this instanceof LivingEntity livingEntity && livingEntity.getType().is(TagInit.RESTRICTED_ENTITY_EXPERIENCE_ENTITIES))) {
            } else {
                LevelExperienceOrbEntity.spawn((ServerLevel) this.level(), this.position(),
                        (int) (this.getBaseExperienceReward() * ConfigInit.CONFIG.mobXPMultiplier
                                * (ConfigInit.CONFIG.dropXPbasedOnLvl && this.lastHurtByPlayer != null
                                ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) this.lastHurtByPlayer).getLevelManager().getOverallLevel()
                                : 1.0F)));
            }
        }
    }

    @Shadow
    protected int getBaseExperienceReward() {
        return 0;
    }

}