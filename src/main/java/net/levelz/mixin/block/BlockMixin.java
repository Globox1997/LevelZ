package net.levelz.mixin.block;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.EntityInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(Block.class)
public class BlockMixin {

    @Nullable
    private ServerPlayerEntity serverPlayerEntity = null;

    @Inject(method = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;"), cancellable = true)
    private static void dropStacksMixin(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo info) {
        if (entity instanceof PlayerEntity) {
            if (EntityInit.isRedstoneBitsLoaded && entity.getClass().getName().contains("RedstoneBitsFakePlayer")) {
                // Redstone bits block breaker compat
            } else {
                if (PlayerStatsManager.listContainsItemOrBlock((PlayerEntity) entity, Registries.BLOCK.getRawId(state.getBlock()), 1)) {
                    info.cancel();
                } else if (stack.getItem() instanceof MiningToolItem) {
                    Item item = stack.getItem();
                    ArrayList<Object> levelList = LevelLists.customItemList;
                    try {
                        if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(item).toString())) {
                            if (!PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) entity, levelList, Registries.ITEM.getId(item).toString(), true)) {
                                info.cancel();
                            }
                        }
                    } catch (AbstractMethodError ignore) {
                    }
                    levelList = null;
                    if (item instanceof AxeItem) {
                        levelList = LevelLists.axeList;
                    } else if (item instanceof HoeItem) {
                        levelList = LevelLists.hoeList;
                    } else if (item instanceof PickaxeItem || item instanceof ShovelItem) {
                        levelList = LevelLists.toolList;
                    }
                    if (levelList != null
                            && !PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) entity, levelList, ((MiningToolItem) stack.getItem()).getMaterial().toString().toLowerCase(), true)) {
                        info.cancel();
                    }
                } else if (stack.getItem() instanceof ShearsItem && !PlayerStatsManager.playerLevelisHighEnough((PlayerEntity) entity, LevelLists.shearsList, null, true)) {
                    info.cancel();
                }
            }
        }
    }

    @Inject(method = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getDroppedStacks(Lnet/minecraft/loot/context/LootContextParameterSet$Builder;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getDroppedStacksMixin(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack,
            CallbackInfoReturnable<List<ItemStack>> info, LootContextParameterSet.Builder builder) {
        if (entity != null && state.getBlock() instanceof ExperienceDroppingBlock && entity instanceof PlayerEntity playerEntity) {
            if ((float) ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager().getSkillLevel(Skill.MINING) * ConfigInit.CONFIG.miningOreChance > world.random.nextFloat()
                    && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0 && state.getDroppedStacks(builder).size() > 0) {
                Block.dropStack(world, pos, state.getDroppedStacks(builder).get(0).split(1));
            }
        }
    }

    @Inject(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"))
    protected void dropExperienceMixin(ServerWorld world, BlockPos pos, int size, CallbackInfo info) {
        if (ConfigInit.CONFIG.oreXPMultiplier > 0.0F)
            LevelExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos),
                    (int) (size * ConfigInit.CONFIG.oreXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
                                    ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager().getOverallLevel()
                                    : 1.0F)));
    }

    @Inject(method = "onBreak", at = @At(value = "HEAD"))
    private void onBreakMixin(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo info) {
        if (!world.isClient)
            serverPlayerEntity = (ServerPlayerEntity) player;
    }

}
