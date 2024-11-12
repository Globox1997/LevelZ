package net.levelz.restriction;

import java.util.Map;

@Deprecated
public class BlockRestriction {

    private final int blockId;
    private final Map<Integer, Integer> skillLevelRestrictions;

    public BlockRestriction(int blockId, Map<Integer, Integer> skillLevelRestrictions) {
        this.blockId = blockId;
        this.skillLevelRestrictions = skillLevelRestrictions;
    }
}
