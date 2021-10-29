package net.levelz.mixin.entity;

import java.util.ArrayList;

import net.levelz.access.ExperienceOrbAccess;
import net.levelz.mixin.misc.ExperienceOrbEntityAccess;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.access.PlayerDropAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected int playerHitTimer;

    @Shadow
    @Nullable
    protected PlayerEntity attackingPlayer;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(method = "applyEnchantmentsToDamage", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Ljava/lang/Iterable;Lnet/minecraft/entity/damage/DamageSource;)I", shift = At.Shift.AFTER), ordinal = 0)
    private int applyEnchantmentsToDamageMixin(int original, DamageSource source, float amount) {
        if (source == DamageSource.FALL && (Object) this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            return (int) (original + ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player).getLevel("agility") * ConfigInit.CONFIG.movementFallBonus);
        } else
            return original;
    }

    @ModifyVariable(method = "tryUseTotem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private ItemStack tryUseTotemMixin(ItemStack original) {
        if ((Object) this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            ArrayList<Object> levelList = LevelLists.totemList;
            if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
                return ItemStack.EMPTY;
            }
        }
        return original;
    }

    @Inject(method = "tryUseTotem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"), cancellable = true)
    private void tryUseTotemMixin(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        if ((Object) this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if (((PlayerStatsManagerAccess) player).getPlayerStatsManager(player).getLevel("luck") >= ConfigInit.CONFIG.maxLevel
                    && player.world.random.nextFloat() < ConfigInit.CONFIG.luckSurviveChance) {
                player.setHealth(1.0F);
                player.clearStatusEffects();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0));
                info.setReturnValue(true);
            }
        }
    }

    @Inject(method = "drop", at = @At(value = "HEAD"), cancellable = true)
    protected void dropMixin(DamageSource source, CallbackInfo info) {
        if (!((Object) this instanceof PlayerEntity) && attackingPlayer != null && this.playerHitTimer > 0 && !((PlayerDropAccess) attackingPlayer).allowMobDrop()) {
            info.cancel();
        }
    }

    @Inject(method = "dropXp", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"), cancellable = true)
    protected void dropXpMixin(CallbackInfo info) {
        if (((Object) this instanceof PlayerEntity)) {
            if (!ConfigInit.CONFIG.dropPlayerXP)
                info.cancel();
            else {
                ExperienceOrbEntity experienceOrbEntity = (ExperienceOrbEntity) EntityType.EXPERIENCE_ORB.create(this.world);
                experienceOrbEntity.refreshPositionAndAngles(this.getBlockPos(), this.random.nextFloat() * 360F, 0.0F);
                experienceOrbEntity.setVelocity((this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D,
                        (this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D);
                ((ExperienceOrbEntityAccess) experienceOrbEntity).setAmount(this.getXpToDrop(this.attackingPlayer));
                ((ExperienceOrbAccess) experienceOrbEntity).setDroppedByPlayer();
                this.world.spawnEntity(experienceOrbEntity);
                info.cancel();
            }
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;drop(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void onDeathMixin(DamageSource source, CallbackInfo info) {
        if (attackingPlayer != null && this.playerHitTimer > 0 && ConfigInit.CONFIG.disableMobFarms) {
            ((PlayerDropAccess) attackingPlayer).increaseKilledMobStat(this.world.getChunk(this.getBlockPos()));
        }
    }

    @Shadow
    protected int getXpToDrop(PlayerEntity player) {
        return 0;
    }

}