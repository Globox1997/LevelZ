package net.levelz.access;

public interface PlayerBreakBlockAccess {

    void setInventoryBlockBreakable(boolean breakable);

    void setAbstractBlockBreakDelta(float breakingDelta);

    float getBreakingAbstractBlockDelta();
}
