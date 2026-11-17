package net.levelz.mixin.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentScreenHandlerMixin {

    @Shadow
    @Final
    public int[] costs;

    @Unique
    private Player playerEntity;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    private void initMixin(int syncId, Inventory playerInventory, ContainerLevelAccess context, CallbackInfo info) {
        this.playerEntity = playerInventory.player;
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;)V", at = @At("TAIL"))
    private void initMixin(int syncId, Inventory playerInventory, CallbackInfo info) {
        this.playerEntity = playerInventory.player;
    }

    @ModifyVariable(method = "getEnchantmentList", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Ljava/util/List;"), index = 6)
    private List<EnchantmentInstance> generateEnchantmentsMixin(List<EnchantmentInstance> original, RegistryAccess registryManager, ItemStack stack, int slot, int level) {
        if (this.playerEntity.isCreative()) {
            return original;
        }
        LevelManager levelManager = ((LevelManagerAccess) this.playerEntity).getLevelManager();

        List<EnchantmentInstance> list = new ArrayList<>();
        for (EnchantmentInstance enchantmentLevelEntry : original) {
            if (levelManager.hasRequiredEnchantmentLevel(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level)) {
                list.add(enchantmentLevelEntry);
            }
        }
        if (list.isEmpty()) {
            Optional<HolderSet.Named<Enchantment>> optional = registryManager.registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
            // rng solution not good :/
            // since mojang changed the enchantment system - this is the most compatible solution which came to my mind
            for (int i = 0; i < 50; i++) {
                List<EnchantmentInstance> enchantmentRng = EnchantmentHelper.selectEnchantment(this.playerEntity.getRandom(), stack, level, ((HolderSet.Named) optional.get()).stream());
                for (EnchantmentInstance enchantmentLevelEntry : enchantmentRng) {
                    if (levelManager.hasRequiredEnchantmentLevel(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level)) {
                        list.add(enchantmentLevelEntry);
                        break;
                    }
                }
                if (!list.isEmpty()) {
                    break;
                }
            }
        }
        return list;
    }


    @Inject(method = "method_17411", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private void method_17411Mixin(ItemStack itemStack, Level world, BlockPos pos, CallbackInfo ci, @Local(ordinal = 1) int j, @Local List<EnchantmentInstance> list) {
        if (list.isEmpty()) {
            this.costs[j] = 0;
        }
    }

}
