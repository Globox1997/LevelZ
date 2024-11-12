package net.levelz.access;

public interface ServerPlayerSyncAccess {

    void syncStats(boolean syncDelay);

    void addLevelExperience(int experience);

}
