package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.levelz.access.ClientPlayerListAccess;
import net.minecraft.client.multiplayer.PlayerInfo;

@Mixin(PlayerInfo.class)
public abstract class ClientPlayerListEntryMixin implements ClientPlayerListAccess {

    @Unique
    private int level;

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }
}
