package net.levelz.access;

import net.minecraft.world.level.chunk.ChunkAccess;

public interface PlayerDropAccess {

    void increaseKilledMobStat(ChunkAccess chunk);

    boolean allowMobDrop();

    void resetKilledMobStat();
}
