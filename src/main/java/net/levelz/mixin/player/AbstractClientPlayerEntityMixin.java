package net.levelz.mixin.player;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerListAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin implements PlayerListAccess {

    @Shadow
    @Nullable
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public int getLevel() {
        if (getPlayerListEntry() != null) {
            return ((PlayerListAccess) getPlayerListEntry()).getLevel();
        }
        return 0;
    }
}
