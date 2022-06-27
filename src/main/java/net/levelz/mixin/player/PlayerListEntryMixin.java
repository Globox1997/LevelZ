package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerListAccess;
import net.minecraft.client.network.PlayerListEntry;

@Environment(EnvType.CLIENT)
@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin implements PlayerListAccess {

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
