package net.levelz.access;

import java.util.Map;
import java.util.UUID;

public interface PlayerListAccess {

    public int getLevel();

    public void setLevel(int level);

    public Map<UUID, Integer> getLevelMap();

}
