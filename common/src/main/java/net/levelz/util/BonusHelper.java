package net.levelz.util;

import net.levelz.access.LevelManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.TagInit;
import net.levelz.level.LevelManager;
import net.levelz.level.SkillBonus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.levelz.mixin.entity.AbstractArrowAccessor;

public class BonusHelper {

    public static void bowBonus(LivingEntity shooter, Projectile projectile) {
        if (shooter instanceof Player playerEntity && projectile instanceof AbstractArrow persistentProjectileEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (LevelManager.BONUSES.containsKey("bowDamage")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("bowDamage");
                int level = levelManager.getSkillLevel(skillBonus.getId());
                if (level >= skillBonus.getLevel()) {
                    persistentProjectileEntity.setBaseDamage(((AbstractArrowAccessor) persistentProjectileEntity).getLevelzBaseDamage() + ConfigInit.CONFIG.bowDamageBonus * level);
                }
            }
            if (LevelManager.BONUSES.containsKey("bowDoubleDamageChance")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("bowDoubleDamageChance");
                int level = levelManager.getSkillLevel(skillBonus.getId());
                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.bowDoubleDamageChanceBonus) {
                    persistentProjectileEntity.setBaseDamage(((AbstractArrowAccessor) persistentProjectileEntity).getLevelzBaseDamage() * 2D);
                }
            }
        }
    }

    public static void crossbowBonus(LivingEntity shooter, Projectile projectile) {
        if (shooter instanceof Player playerEntity && projectile instanceof AbstractArrow persistentProjectileEntity) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (LevelManager.BONUSES.containsKey("crossbowDamage")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("crossbowDamage");
                int level = levelManager.getSkillLevel(skillBonus.getId());
                if (level >= skillBonus.getLevel()) {
                    persistentProjectileEntity.setBaseDamage(((AbstractArrowAccessor) persistentProjectileEntity).getLevelzBaseDamage() + ConfigInit.CONFIG.crossbowDamageBonus * level);
                }
            }
            if (LevelManager.BONUSES.containsKey("crossbowDoubleDamageChance")) {
                SkillBonus skillBonus = LevelManager.BONUSES.get("crossbowDoubleDamageChance");
                int level = levelManager.getSkillLevel(skillBonus.getId());
                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.crossbowDoubleDamageChanceBonus) {
                    persistentProjectileEntity.setBaseDamage(((AbstractArrowAccessor) persistentProjectileEntity).getLevelzBaseDamage() * 2D);
                }
            }
        }
    }

    public static boolean itemDamageChanceBonus(@Nullable Player playerEntity) {
        if (playerEntity != null && LevelManager.BONUSES.containsKey("itemDamageChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("itemDamageChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.itemDamageChanceBonus * level) {
                return true;
            }
        }
        return false;
    }

    public static MobEffectInstance potionEffectChanceBonus(@Nullable Player playerEntity, MobEffectInstance statusEffectInstance) {
        if (playerEntity != null && LevelManager.BONUSES.containsKey("potionEffectChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("potionEffectChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.potionEffectChanceBonus) {
                return new MobEffectInstance(statusEffectInstance.getEffect(), statusEffectInstance.getDuration(),
                        statusEffectInstance.getAmplifier() + 1, statusEffectInstance.isAmbient(),
                        statusEffectInstance.isVisible(), statusEffectInstance.showIcon());
            }
        }
        return statusEffectInstance;
    }

    public static void breedTwinChanceBonus(ServerLevel world, Player playerEntity, AgeableMob animalEntity, AgeableMob otherAnimalEntity) {
        if (LevelManager.BONUSES.containsKey("breedTwinChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("breedTwinChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.twinBreedChanceBonus) {
                AgeableMob extraPassiveEntity = animalEntity.getBreedOffspring(world, otherAnimalEntity);
                if (extraPassiveEntity == null) {
                    return;
                }
                extraPassiveEntity.setBaby(true);
                extraPassiveEntity.snapTo(animalEntity.getX(), animalEntity.getY(), animalEntity.getZ(), playerEntity.getRandom().nextFloat() * 360F, 0.0F);
                world.addFreshEntityWithPassengers(extraPassiveEntity);
            }
        }
    }

    public static float fallDamageReductionBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("fallDamageReduction")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("fallDamageReduction");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                return level * ConfigInit.CONFIG.fallDamageReductionBonus;
            }
        }
        return 0.0f;
    }

    public static boolean deathGraceChanceBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("deathGraceChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("deathGraceChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.deathGraceChanceBonus) {
                playerEntity.setHealth(1.0F);
                playerEntity.removeAllEffects();
                playerEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                playerEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));
                return true;
            }
        }

        return false;
    }

    public static float tntStrengthBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("tntStrength")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("tntStrength");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                return ConfigInit.CONFIG.tntStrengthBonus;
            }
        }
        return 0.0f;
    }

    public static float priceDiscountBonus(Player playerEntity) {
        if (playerEntity.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            return 1.0f;
        }
        if (LevelManager.BONUSES.containsKey("priceDiscount")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("priceDiscount");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                return 1.0f - (level * ConfigInit.CONFIG.priceDiscountBonus);
            }
        }
        return 1.0f;
    }

    public static void tradeXpBonus(ServerLevel serverWorld, @Nullable Player playerEntity, AbstractVillager merchantEntity, int amount) {
        amount = (int) (amount * ConfigInit.CONFIG.tradingXPMultiplier);
        if (amount > 0) {
            if (playerEntity != null) {
                if (LevelManager.BONUSES.containsKey("tradeXp")) {
                    LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
                    SkillBonus skillBonus = LevelManager.BONUSES.get("tradeXp");
                    int level = levelManager.getSkillLevel(skillBonus.getId());
                    if (level >= skillBonus.getLevel()) {
                        amount = (int) (amount * (1.0f + level * ConfigInit.CONFIG.tradeXpBonus));
                    }
                }
            }
            LevelExperienceOrbEntity.spawn(serverWorld, merchantEntity.position().add(0.0D, 0.5D, 0.0D), amount);
            // Todo: HERE
            // ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) lastCustomer).getPlayerStatsManager().getOverallLevel()
        }
    }

    public static boolean merchantImmuneBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("merchantImmune")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("merchantImmune");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                return true;
            }
        }
        return false;
    }

    public static void miningDropChanceBonus(Player playerEntity, BlockState state, BlockPos pos, LootParams.Builder builder) {
        if (state.is(TagInit.ORES) && EnchantmentHelper.getEnchantmentLevel(playerEntity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), playerEntity) <= 0) {
            if (LevelManager.BONUSES.containsKey("miningDropChance")) {
                LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
                SkillBonus skillBonus = LevelManager.BONUSES.get("miningDropChance");
                int level = levelManager.getSkillLevel(skillBonus.getId());
                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.miningDropChanceBonus) {
                    List<ItemStack> list = state.getDrops(builder);
                    if (!list.isEmpty()) {
                        Block.popResource(playerEntity.level(), pos, list.getFirst().copyWithCount(1));
                    }
                }
            }
        }
    }

    public static void plantDropChanceBonus(Player playerEntity, BlockState state, BlockPos pos) {
        if (EnchantmentHelper.getEnchantmentLevel(playerEntity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), playerEntity) <= 0) {
            if (LevelManager.BONUSES.containsKey("plantDropChance")) {
                LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
                SkillBonus skillBonus = LevelManager.BONUSES.get("plantDropChance");
                int level = levelManager.getSkillLevel(skillBonus.getId());
                if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.plantDropChanceBonus) {
                    List<ItemStack> list = Block.getDrops(state, (ServerLevel) playerEntity.level(), pos, null);
                    for (ItemStack itemStack : list) {
                        if (itemStack.is(TagInit.CROPS)) {
                            Block.popResource(playerEntity.level(), pos, itemStack);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static boolean anvilXpCapBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("anvilXpCap")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("anvilXpCap");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                return true;
            }
        }
        return false;
    }

    public static int anvilXpDiscountBonus(Player playerEntity, int levelCost) {
        if (levelCost > ConfigInit.CONFIG.anvilXpCap && anvilXpCapBonus(playerEntity)) {
            return ConfigInit.CONFIG.anvilXpCap;
        }
        if (LevelManager.BONUSES.containsKey("anvilXpDiscount")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("anvilXpDiscount");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                return (int) (levelCost * (1.0f - level * ConfigInit.CONFIG.anvilXpDiscountBonus));
            }
        }
        return levelCost;
    }

    public static boolean anvilXpChanceBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("anvilXpChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("anvilXpChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.anvilXpChanceBonus) {
                return true;
            }
        }
        return false;
    }

    public static void healthRegenBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("healthRegen")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("healthRegen");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                playerEntity.heal(level * ConfigInit.CONFIG.healthRegenBonus);
            }
        }
    }

    public static void healthAbsorptionBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("healthAbsorption")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("healthAbsorption");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                playerEntity.setAbsorptionAmount(ConfigInit.CONFIG.healthAbsorptionBonus);
            }
        }
    }

    public static float exhaustionReductionBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("exhaustionReduction")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("exhaustionReduction");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                return Math.max(1.0f - (level * ConfigInit.CONFIG.exhaustionReductionBonus), 0.0f);
            }
        }
        return 1.0f;
    }

    public static boolean meleeKnockbackAttackChanceBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("meleeKnockbackAttackChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("meleeKnockbackAttackChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.meleeKnockbackAttackChanceBonus) {
                return true;
            }
        }
        return false;
    }

    public static boolean meleeCriticalAttackChanceBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("meleeCriticalAttackChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("meleeCriticalAttackChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.meleeCriticalAttackChanceBonus) {
                return true;
            }
        }
        return false;
    }

    public static float meleeCriticalDamageBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("meleeCriticalAttackDamage")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("meleeCriticalAttackDamage");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                return level * ConfigInit.CONFIG.meleeCriticalAttackDamageBonus;
            }
        }
        return 0.0f;
    }

    public static boolean meleeDoubleDamageBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("meleeDoubleAttackDamageChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("meleeDoubleAttackDamageChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.meleeDoubleAttackDamageChanceBonus) {
                return true;
            }
        }
        return false;
    }

    public static void foodIncreasionBonus(Player playerEntity, ItemStack itemStack) {
        if (LevelManager.BONUSES.containsKey("foodIncreasion") && itemStack.get(DataComponents.FOOD) != null) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("foodIncreasion");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel()) {
                FoodProperties foodComponent = itemStack.get(DataComponents.FOOD);
                float multiplier = level * ConfigInit.CONFIG.foodIncreasionBonus;
                playerEntity.getFoodData().eat(new FoodProperties((int) (foodComponent.nutrition() * multiplier), foodComponent.saturation() * multiplier, true));
            }
        }
    }

    public static void damageReflectionBonus(Player playerEntity, DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker != null && attacker != playerEntity && !source.is(DamageTypes.THORNS) && LevelManager.BONUSES.containsKey("damageReflection") && LevelManager.BONUSES.containsKey("damageReflectionChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("damageReflectionChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.CONFIG.damageReflectionChanceBonus) {
                skillBonus = LevelManager.BONUSES.get("damageReflection");
                level = levelManager.getSkillLevel(skillBonus.getId());
                if (level >= skillBonus.getLevel()) {
                    attacker.hurt(playerEntity.damageSources().thorns(playerEntity), amount * level * ConfigInit.CONFIG.damageReflectionBonus);
                }
            }
        }
    }

    public static boolean evadingDamageBonus(Player playerEntity) {
        if (LevelManager.BONUSES.containsKey("evadingDamageChance")) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("evadingDamageChance");
            int level = levelManager.getSkillLevel(skillBonus.getId());
            if (level >= skillBonus.getLevel() && playerEntity.getRandom().nextFloat() <= ConfigInit.CONFIG.evadingDamageChanceBonus) {
                return true;
            }
        }
        return false;
    }


}
