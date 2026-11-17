package net.levelz.mixin.misc;

import net.levelz.access.PlayerDropAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RandomizableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {

    @Inject(method = "canOpen", at = @At("RETURN"))
    private void checkUnlockedMixin(Player player, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue()) {
            ((PlayerDropAccess) player).resetKilledMobStat();
        }
    }
}
