package net.levelz.mixin.misc;

import net.levelz.access.PlayerDropAccess;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {

    @Inject(method = "checkUnlocked", at = @At("RETURN"))
    private void checkUnlockedMixin(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue()) {
            ((PlayerDropAccess) player).resetKilledMobStat();
        }
    }
}
