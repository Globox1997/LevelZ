package net.levelz.mixin.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {

    @Shadow
    @Final
    public int[] enchantmentPower;

    @Unique
    private PlayerEntity playerEntity;

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
    private void initMixin(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo info) {
        this.playerEntity = playerInventory.player;
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;)V", at = @At("TAIL"))
    private void initMixin(int syncId, PlayerInventory playerInventory, CallbackInfo info) {
        this.playerEntity = playerInventory.player;
    }

    @ModifyVariable(method = "generateEnchantments", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;generateEnchantments(Lnet/minecraft/util/math/random/Random;Lnet/minecraft/item/ItemStack;ILjava/util/stream/Stream;)Ljava/util/List;"), index = 6)
    private List<EnchantmentLevelEntry> generateEnchantmentsMixin(List<EnchantmentLevelEntry> original, DynamicRegistryManager registryManager, ItemStack stack, int slot, int level) {
        if (this.playerEntity.isCreative()) {
            return original;
        }
        LevelManager levelManager = ((LevelManagerAccess) this.playerEntity).getLevelManager();

        List<EnchantmentLevelEntry> list = new ArrayList<>();
        for (EnchantmentLevelEntry enchantmentLevelEntry : original) {
            if (levelManager.hasRequiredEnchantmentLevel(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level)) {
                list.add(enchantmentLevelEntry);
            }
        }
        if (list.isEmpty()) {
            Optional<RegistryEntryList.Named<Enchantment>> optional = registryManager.get(RegistryKeys.ENCHANTMENT).getEntryList(EnchantmentTags.IN_ENCHANTING_TABLE);
            // rng solution not good :/
            // since mojang changed the enchantment system - this is the most compatible solution which came to my mind
            for (int i = 0; i < 50; i++) {
                List<EnchantmentLevelEntry> enchantmentRng = EnchantmentHelper.generateEnchantments(this.playerEntity.getRandom(), stack, level, ((RegistryEntryList.Named) optional.get()).stream());
                for (EnchantmentLevelEntry enchantmentLevelEntry : enchantmentRng) {
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
    private void method_17411Mixin(ItemStack itemStack, World world, BlockPos pos, CallbackInfo ci, @Local(ordinal = 1) int j, @Local List<EnchantmentLevelEntry> list) {
        if (list.isEmpty()) {
            this.enchantmentPower[j] = 0;
        }
    }

}
