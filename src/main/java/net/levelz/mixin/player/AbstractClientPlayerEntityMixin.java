package net.levelz.mixin.player;

import net.levelz.access.ClientPlayerAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.ClientPlayerListAccess;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin implements ClientPlayerListAccess, ClientPlayerAccess {

    @Unique
    private boolean shouldRenderClientName = true;

    @Shadow
    @Nullable
    protected PlayerInfo getPlayerInfo() {
        return null;
    }

    @Override
    public int getLevel() {
        if (getPlayerInfo() != null) {
            return ((ClientPlayerListAccess) getPlayerInfo()).getLevel();
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
