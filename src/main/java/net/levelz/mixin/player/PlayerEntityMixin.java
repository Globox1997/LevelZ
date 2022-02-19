package net.levelz.mixin.player;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.levelz.access.ExperienceOrbAccess;
import net.levelz.access.PlayerDropAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.network.PlayerStatsServerPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerStatsManagerAccess, PlayerDropAccess {

    private final PlayerStatsManager playerStatsManager = new PlayerStatsManager();
    private final PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    private boolean isCrit;
    private int killedMobsInChunk;
    @Nullable
    private Chunk killedMobChunk;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readCustomDataFromNbtMixin(NbtCompound tag, CallbackInfo info) {
        this.playerStatsManager.readNbt(tag);
        playerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerStatsManager.getLevel("agility") * ConfigInit.CONFIG.movementBonus);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbtMixin(NbtCompound tag, CallbackInfo info) {
        this.playerStatsManager.writeNbt(tag);
    }

    @Inject(method = "addExperience", at = @At(value = "TAIL"))
    public void addExperienceMixin(int experience, CallbackInfo info) {
        boolean isEndLvl = this.playerStatsManager.isMaxLevel();
        if (!isEndLvl) {
            playerStatsManager.levelProgress += (float) experience / (float) playerStatsManager.getNextLevelExperience();
            playerStatsManager.totalLevelExperience = MathHelper.clamp(playerStatsManager.totalLevelExperience + experience, 0, Integer.MAX_VALUE);
            while (playerStatsManager.levelProgress >= 1.0F && !isEndLvl) {
                playerStatsManager.levelProgress = (playerStatsManager.levelProgress - 1.0F) * (float) playerStatsManager.getNextLevelExperience();
                playerStatsManager.addExperienceLevels(1);
                playerStatsManager.levelProgress /= (float) playerStatsManager.getNextLevelExperience();
                if (playerEntity instanceof ServerPlayerEntity) {
                    PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, (ServerPlayerEntity) playerEntity);
                    PlayerStatsManager.onLevelUp(playerEntity, playerStatsManager.overallLevel);
                }
                if (playerStatsManager.overallLevel > 0) {
                    playerEntity.world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, playerEntity.getSoundCategory(), 1.0F, 1.0F);
                }
            }
        }
    }

    @Redirect(method = "addExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), require = 0)
    private void addExhaustion(HungerManager hungerManager, float exhaustion) {
        exhaustion *= ConfigInit.CONFIG.staminaBase - ((float) playerStatsManager.getLevel("stamina") * ConfigInit.CONFIG.staminaBonus);
        hungerManager.addExhaustion(exhaustion);
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), ordinal = 2, require = 0)
    private boolean attackMixin(boolean original) {
        if (playerEntity.world.random.nextFloat() < (float) playerStatsManager.getLevel("luck") * ConfigInit.CONFIG.luckCritBonus) {
            isCrit = true;
            return true;
        } else
            isCrit = false;
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", shift = At.Shift.AFTER), ordinal = 0, require = 0)
    private float attackGetItemMixin(float original) {
        if (playerStatsManager.getLevel("strength") >= ConfigInit.CONFIG.maxLevel && ConfigInit.CONFIG.attackDoubleDamageChance > playerEntity.world.random.nextFloat()) {
            return original * 2F;
        } else
            return isCrit ? original * ConfigInit.CONFIG.critDmgBonus : original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"), ordinal = 0, require = 0)
    private float attackGetAttackCooldownProgressMixin(float original) {
        return getUnlockedDamage(original, false);
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"), ordinal = 1, require = 0)
    private float attackGetAttackCooldownProgressMixinTwo(float original) {
        return getUnlockedDamage(original, true);
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getFireAspect(Lnet/minecraft/entity/LivingEntity;)I"), ordinal = 1, require = 0)
    private int attackGetFireAspectMixin(int original) {
        return (int) getUnlockedDamage((float) original, true);
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getKnockback(Lnet/minecraft/entity/LivingEntity;)I"), ordinal = 0, require = 0)
    private int attackGetKnockbackMixin(int original) {
        return (int) getUnlockedDamage((float) original, true);
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getNonSpectatingEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;"), ordinal = 3)
    private float attackGetSweepingMultiplierMixin(float original) {
        return getUnlockedDamage(original, true);
    }

    private float getUnlockedDamage(float original, boolean zero) {
        if (playerEntity.getMainHandStack().getItem() instanceof ToolItem) {
            ArrayList<Object> levelList = null;
            if (playerEntity.getMainHandStack().isIn(FabricToolTags.SWORDS)) {
                levelList = LevelLists.swordList;
            } else if (playerEntity.getMainHandStack().isIn(FabricToolTags.AXES))
                levelList = LevelLists.axeList;
            else if (playerEntity.getMainHandStack().isIn(FabricToolTags.HOES))
                levelList = LevelLists.hoeList;
            else if (playerEntity.getMainHandStack().isIn(FabricToolTags.PICKAXES) || playerEntity.getMainHandStack().isIn(FabricToolTags.SHOVELS))
                levelList = LevelLists.toolList;
            if (levelList != null)
                if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((ToolItem) playerEntity.getMainHandStack().getItem()).getMaterial().toString().toLowerCase(), true))
                    return zero ? 0 : 1.0F;
        }
        return original;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V", shift = Shift.AFTER), cancellable = true)
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (playerStatsManager.getLevel("defense") >= ConfigInit.CONFIG.maxLevel && source.getAttacker() != null && playerEntity.world.random.nextFloat() <= ConfigInit.CONFIG.defenseReflectChance) {
            source.getAttacker().damage(source, amount);
        }
        if (playerStatsManager.getLevel("agility") >= ConfigInit.CONFIG.maxLevel && playerEntity.world.random.nextFloat() <= ConfigInit.CONFIG.movementMissChance) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    private void eatFoodMixin(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        if (stack.getItem().isFood() && playerStatsManager.getLevel("stamina") >= ConfigInit.CONFIG.maxLevel) {
            FoodComponent foodComponent = stack.getItem().getFoodComponent();
            float multiplier = ConfigInit.CONFIG.staminaFoodBonus;
            playerEntity.getHungerManager().add((int) (foodComponent.getHunger() * multiplier), foodComponent.getSaturationModifier() * multiplier);
        }
    }

    @Override
    public PlayerStatsManager getPlayerStatsManager(PlayerEntity player) {
        return this.playerStatsManager;
    }

    @Override
    public void increaseKilledMobStat(Chunk chunk) {
        if (killedMobChunk != null && killedMobChunk == chunk) {
            killedMobsInChunk++;
        } else {
            killedMobChunk = chunk;
            killedMobsInChunk = 0;
        }
    }

    @Override
    public void resetKilledMobStat() {
        killedMobsInChunk = 0;
    }

    @Override
    public boolean allowMobDrop() {
        return killedMobsInChunk >= 5 ? false : true;
    }

    @Override
    protected void dropXp() {
        if (ConfigInit.CONFIG.dropPlayerXP) {
            ExperienceOrbEntity experienceOrbEntity = (ExperienceOrbEntity) EntityType.EXPERIENCE_ORB.create(this.world);
            experienceOrbEntity.refreshPositionAndAngles(this.getBlockPos(), this.random.nextFloat() * 360F, 0.0F);
            experienceOrbEntity.setVelocity((this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D,
                    (this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D);
            ((ExperienceOrbAccess) experienceOrbEntity).setAmount(this.getXpToDrop(this.attackingPlayer));
            ((ExperienceOrbAccess) experienceOrbEntity).setDroppedByPlayer();
            this.world.spawnEntity(experienceOrbEntity);
        }
    }

}