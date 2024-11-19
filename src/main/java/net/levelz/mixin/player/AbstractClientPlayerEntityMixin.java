package net.levelz.mixin.player;

import net.levelz.access.ClientPlayerAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.ClientPlayerListAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin implements ClientPlayerListAccess, ClientPlayerAccess {

    @Unique
    private boolean shouldRenderClientName = true;

    @Shadow
    @Nullable
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public int getLevel() {
        if (getPlayerListEntry() != null) {
            return ((ClientPlayerListAccess) getPlayerListEntry()).getLevel();
        }
        return 0;
    }

    @Override
    public boolean shouldRenderClientName() {
        return this.shouldRenderClientName;
    }

    @Override
    public void setShouldRenderClientName(boolean shouldRenderClientName) {
        this.shouldRenderClientName = shouldRenderClientName;
    }
}
