package net.levelz.mixin.compat;

import net.levelz.level.LevelManager;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelManager.class)
public class LevelManagerCompatMixin {

    @Shadow
    @Mutable
    @Final
    private PlayerEntity playerEntity;

    @Inject(method = "hasRequiredBlockLevel", at = @At("HEAD"), cancellable = true)
    private void hasRequiredBlockLevelMixin(Block block, CallbackInfoReturnable<Boolean> info) {
        if (this.playerEntity.getClass().getName().contains("deployer.DeployerFakePlayer") || this.playerEntity.getClass().getName().contains("core.TurtlePlayer")) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "hasRequiredCraftingLevel", at = @At("HEAD"), cancellable = true)
    private void hasRequiredCraftingLevelMixin(Item item, CallbackInfoReturnable<Boolean> info) {
        if (this.playerEntity.getClass().getName().contains("deployer.DeployerFakePlayer") || this.playerEntity.getClass().getName().contains("core.TurtlePlayer")) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "hasRequiredEntityLevel", at = @At("HEAD"), cancellable = true)
    private void hasRequiredEntityLevelMixin(EntityType<?> entityType, CallbackInfoReturnable<Boolean> info) {
        if (this.playerEntity.getClass().getName().contains("deployer.DeployerFakePlayer") || this.playerEntity.getClass().getName().contains("core.TurtlePlayer")) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "hasRequiredItemLevel", at = @At("HEAD"), cancellable = true)
    private void hasRequiredItemLevelMixin(Item item, CallbackInfoReturnable<Boolean> info) {
        if (this.playerEntity.getClass().getName().contains("deployer.DeployerFakePlayer") || this.playerEntity.getClass().getName().contains("core.TurtlePlayer")) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "hasRequiredMiningLevel", at = @At("HEAD"), cancellable = true)
    private void hasRequiredMiningLevelMixin(Block block, CallbackInfoReturnable<Boolean> info) {
        if (this.playerEntity.getClass().getName().contains("deployer.DeployerFakePlayer") || this.playerEntity.getClass().getName().contains("core.TurtlePlayer")) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "hasRequiredEnchantmentLevel", at = @At("HEAD"), cancellable = true)
    private void hasRequiredEnchantmentLevelMixin(RegistryEntry<Enchantment> enchantment, int level, CallbackInfoReturnable<Boolean> info) {
        if (this.playerEntity.getClass().getName().contains("deployer.DeployerFakePlayer") || this.playerEntity.getClass().getName().contains("core.TurtlePlayer")) {
            info.setReturnValue(true);
        }
    }

}
