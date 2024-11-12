package net.levelz.mixin.misc;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    // Used for loot stuff lol?
//    @ModifyVariable(method = "getEquipmentLevel", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;"), ordinal = 0)
//    private static int getEquipmentLevelMixin(int original, Enchantment enchantment, LivingEntity entity) {
////        if (original != 0 && entity instanceof PlayerEntity player && (float) ((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(SkillOld.ALCHEMY)
////                * ConfigInit.CONFIG.alchemyEnchantmentChance > entity.getWorld().getRandom().nextFloat())
////            return original += 1;
////        else
////            return original;
//    }

    @Inject(method = "onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void onTargetDamagedMixin(ServerWorld world, Entity target, DamageSource damageSource, ItemStack weapon, CallbackInfo info) {
        if (weapon != null && damageSource.getAttacker() instanceof PlayerEntity playerEntity) {
            LevelManager levelManager = ((LevelManagerAccess)playerEntity).getLevelManager();
            if (!levelManager.hasRequiredItemLevel(weapon.getItem())) {
                info.cancel();
            }
        }
    }

//    @Inject(method = "onUserDamaged", at = @At("HEAD"), cancellable = true)
//    private static void onUserDamagedMixin(LivingEntity user, Entity attacker, CallbackInfo info) {
//        if (user instanceof PlayerEntity) {
//            Item item = user.getStackInHand(user.getActiveHand()).getItem();
//            if (item instanceof ToolItem) {
//                PlayerEntity playerEntity = (PlayerEntity) user;
//                ArrayList<Object> levelList = LevelLists.customItemList;
//                if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(item).toString())) {
//                    if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, Registries.ITEM.getId(item).toString(), true))
//                        info.cancel();
//                } else {
//                    levelList = null;
//                    if (item instanceof SwordItem) {
//                        levelList = LevelLists.swordList;
//                    } else if (item instanceof AxeItem)
//                        levelList = LevelLists.axeList;
//                    else if (item instanceof HoeItem)
//                        levelList = LevelLists.hoeList;
//                    else if (item instanceof PickaxeItem || item instanceof ShovelItem)
//                        levelList = LevelLists.toolList;
//                    if (levelList != null)
//                        if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((ToolItem) item).getMaterial().toString().toLowerCase(), true))
//                            info.cancel();
//                }
//            }
//        }
//    }
}
