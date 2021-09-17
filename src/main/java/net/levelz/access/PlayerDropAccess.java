package net.levelz.access;

import net.minecraft.world.chunk.Chunk;

public interface PlayerDropAccess {

    public void increaseKilledMobStat(Chunk chunk);

    public boolean allowMobDrop();

    public void resetKilledMobStat();
}
