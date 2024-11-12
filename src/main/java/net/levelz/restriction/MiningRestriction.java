package net.levelz.restriction;

import java.util.Map;

@Deprecated
public class MiningRestriction {

    private final int blockId;
    private final Map<Integer, Integer> skillLevelRestrictions;

    public MiningRestriction(int blockId, Map<Integer, Integer> skillLevelRestrictions) {
        this.blockId = blockId;
        this.skillLevelRestrictions = skillLevelRestrictions;
    }
}
