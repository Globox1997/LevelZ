package net.levelz.access;

public interface PlayerSyncAccess {

    void syncStats(boolean syncDelay);

    void addLevelExperience(int experience);

    void levelUp(int levels, boolean deductXp, boolean ignoreMaxLevel);
}
