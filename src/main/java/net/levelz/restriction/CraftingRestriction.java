package net.levelz.restriction;

import java.util.Map;

@Deprecated
public class CraftingRestriction {

    // output item
    private final int itemId;
    private final Map<Integer, Integer> skillLevelRestrictions;

    public CraftingRestriction(int itemId, Map<Integer, Integer> skillLevelRestrictions) {
        this.itemId = itemId;
        this.skillLevelRestrictions = skillLevelRestrictions;
    }
}
