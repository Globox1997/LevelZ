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

import net.levelz.access.PlayerDropAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.AxeItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerStatsManagerAccess, PlayerDropAccess {

    private final PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    private final PlayerStatsManager playerStatsManager = new PlayerStatsManager(playerEntity);
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
                .setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerStatsManager.getSkillLevel(Skill.AGILITY) * ConfigInit.CONFIG.movementBonus);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbtMixin(NbtCompound tag, CallbackInfo info) {
        this.playerStatsManager.writeNbt(tag);
    }

    @Redirect(method = "addExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), require = 0)
    private void addExhaustion(HungerManager hungerManager, float exhaustion) {
        exhaustion *= ConfigInit.CONFIG.staminaBase - ((float) playerStatsManager.getSkillLevel(Skill.STAMINA) * ConfigInit.CONFIG.staminaBonus);
        hungerManager.addExhaustion(exhaustion);
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getKnockback(Lnet/minecraft/entity/LivingEntity;)I"), ordinal = 0, require = 0)
    private boolean attacGetKnockbackkMixin(boolean original) {
        if (playerEntity.getWorld().getRandom().nextFloat() < (float) playerStatsManager.getSkillLevel(Skill.LUCK) * ConfigInit.CONFIG.luckCritBonus) {
            isCrit = true;
            return true;
        } else
            isCrit = false;
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSprinting()Z", ordinal = 0), ordinal = 1, require = 0)
    private boolean attackIsSprintingMixin(boolean original) {
        if (isCrit) {
            return true;
        } else
            return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getFireAspect(Lnet/minecraft/entity/LivingEntity;)I"), ordinal = 2, require = 0)
    private boolean attackGetFireAspectMixin(boolean original) {
        if (isCrit) {
            return true;
        } else
            return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", shift = At.Shift.AFTER), ordinal = 0, require = 0)
    private float attackGetItemMixin(float original) {
        if (playerStatsManager.getSkillLevel(Skill.STRENGTH) >= ConfigInit.CONFIG.maxLevel && ConfigInit.CONFIG.attackDoubleDamageChance > playerEntity.getWorld().getRandom().nextFloat()) {
            return original * 2F;
        } else
            return isCrit ? original * ConfigInit.CONFIG.attackCritDmgBonus : original;
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
        Item item = playerEntity.getMainHandStack().getItem();
        if (!item.equals(Items.AIR)) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(item).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, Registries.ITEM.getId(item).toString(), true))
                    return zero ? 0 : 1.0F;
            } else if (item instanceof ToolItem) {
                levelList = null;
                if (item instanceof SwordItem) {
                    levelList = LevelLists.swordList;
                } else if (item instanceof AxeItem) {
                    if (ConfigInit.CONFIG.bindAxeDamageToSwordRestriction)
                        levelList = LevelLists.swordList;
                    else
                        levelList = LevelLists.axeList;
                } else if (item instanceof HoeItem)
                    levelList = LevelLists.hoeList;
                else if (item instanceof PickaxeItem || item instanceof ShovelItem)
                    levelList = LevelLists.toolList;
                if (levelList != null)
                    if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((ToolItem) item).getMaterial().toString().toLowerCase(), true))
                        return zero ? 0 : 1.0F;
            }
        }
        return original;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V", shift = Shift.AFTER), cancellable = true)
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (playerStatsManager.getSkillLevel(Skill.DEFENSE) >= ConfigInit.CONFIG.maxLevel && source.getAttacker() != null
                && playerEntity.getWorld().getRandom().nextFloat() <= ConfigInit.CONFIG.defenseReflectChance) {
            source.getAttacker().damage(source, amount);
        }
        if (playerStatsManager.getSkillLevel(Skill.AGILITY) >= ConfigInit.CONFIG.maxLevel && playerEntity.getWorld().getRandom().nextFloat() <= ConfigInit.CONFIG.movementMissChance) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    private void eatFoodMixin(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        if (stack.getItem().isFood() && playerStatsManager.getSkillLevel(Skill.STAMINA) >= ConfigInit.CONFIG.maxLevel) {
            FoodComponent foodComponent = stack.getItem().getFoodComponent();
            float multiplier = ConfigInit.CONFIG.staminaFoodBonus;
            playerEntity.getHungerManager().add((int) (foodComponent.getHunger() * multiplier), foodComponent.getSaturationModifier() * multiplier);
        }
    }

    @Override
    public PlayerStatsManager getPlayerStatsManager() {
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
        return killedMobsInChunk >= ConfigInit.CONFIG.mobKillCount ? false : true;
    }

    @Override
    protected void dropXp() {
        if (this.getWorld() instanceof ServerWorld
                && (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.shouldDropXp() && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
            if (ConfigInit.CONFIG.dropPlayerXP && (ConfigInit.CONFIG.resetCurrentXP || ConfigInit.CONFIG.hardMode))
                LevelExperienceOrbEntity.spawn((ServerWorld) this.getWorld(), this.getPos(), (int) (playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience()));
            ExperienceOrbEntity.spawn((ServerWorld) this.getWorld(), this.getPos(), this.getXpToDrop());
        }
    }

}