package net.levelz.mixin.player;

import net.levelz.access.LevelManagerAccess;
import net.levelz.access.PlayerDropAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.util.BonusHelper;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements LevelManagerAccess, PlayerDropAccess {

    @Unique
    private final Player playerEntity = (Player) (Object) this;
    @Unique
    private final LevelManager levelManager = new LevelManager(playerEntity);

    @Unique
    private int killedMobsInChunk;
    @Unique
    @Nullable
    private ChunkPos killedMobChunkPos;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    public void readCustomDataFromNbtMixin(ValueInput nbt, CallbackInfo info) {
        this.levelManager.readNbt(nbt);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    public void writeCustomDataToNbtMixin(ValueOutput nbt, CallbackInfo info) {
        this.levelManager.writeNbt(nbt);
    }

    @ModifyVariable(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"), ordinal = 0, argsOnly = true)
    private float addExhaustionMixin(float original) {
        original *= BonusHelper.exhaustionReductionBonus(this.playerEntity);
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/Item;getAttackDamageBonus(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)F"), ordinal = 0)
    private boolean attackKnockbackkMixin(boolean original) {
        if (!original && BonusHelper.meleeKnockbackAttackChanceBonus(this.playerEntity)) {
            return true;
        }
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/Item;getAttackDamageBonus(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)F"), ordinal = 1)
    private boolean attackCriticalMixin(boolean original) {
        if (!original && BonusHelper.meleeCriticalAttackChanceBonus(this.playerEntity)) {
            return true;
        }
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getWeaponItem()Lnet/minecraft/world/item/ItemStack;"), ordinal = 0)
    private float attackMixin(float original) {
        if (this.playerEntity.isCreative()) {
            return original;
        }
        if (!levelManager.hasRequiredStackLevel(getWeaponItem())) {
            return 0.0f;
        }
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;playServerSideSound(Lnet/minecraft/sounds/SoundEvent;)V", ordinal = 0), ordinal = 0)
    private float attackCriticalDamageMixin(float original) {
        original += BonusHelper.meleeCriticalDamageBonus(this.playerEntity);
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"), ordinal = 3)
    private float attackDoubleDamageMixin(float original) {
        if (BonusHelper.meleeDoubleDamageBonus(this.playerEntity)) {
            original *= 2f;
        }
        return original;
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"), cancellable = true)
    private void damageMixin(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && BonusHelper.evadingDamageBonus(this.playerEntity)) {
            info.setReturnValue(false);
            return;
        }
        if (this.getInvulnerableTime() > 10 && amount <= this.lastHurt) {
            return;
        }
        BonusHelper.damageReflectionBonus(this.playerEntity, source, amount);
    }

    @Shadow
    public abstract ItemStack getWeaponItem();


    @Override
    public LevelManager getLevelManager() {
        return this.levelManager;
    }

    @Override
    public void increaseKilledMobStat(ChunkAccess chunk) {
        if (chunk.getPos().equals(killedMobChunkPos)) {
            killedMobsInChunk++;
        } else {
            killedMobChunkPos = chunk.getPos();
            killedMobsInChunk = 0;
        }
    }

    @Override
    public void resetKilledMobStat() {
        killedMobsInChunk = 0;
    }

    @Override
    public boolean allowMobDrop() {
        return killedMobsInChunk < ConfigInit.CONFIG.mobKillCount;
    }

    @Override
    protected void dropExperience(ServerLevel serverWorld, @Nullable Entity attacker) {
        if (this.shouldDropExperience() && serverWorld.getGameRules().get(GameRules.MOB_DROPS) && ConfigInit.CONFIG.resetCurrentXp) {
            LevelExperienceOrbEntity.spawn(serverWorld, this.position(), (int) (this.levelManager.getLevelProgress() * this.levelManager.getNextLevelExperience() * Mth.clamp(ConfigInit.CONFIG.currentXpLossPercentage, 0.0F, 100.0F) / 100.0F));
        }
        super.dropExperience(serverWorld, attacker);
    }

}