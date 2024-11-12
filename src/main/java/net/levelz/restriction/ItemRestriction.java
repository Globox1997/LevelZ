package net.levelz.restriction;

import java.util.Map;

@Deprecated
public class ItemRestriction {

    private final int itemId;
    private final Map<Integer, Integer> skillLevelRestrictions;

    public ItemRestriction(int itemId, Map<Integer, Integer> skillLevelRestrictions) {
        this.itemId = itemId;
        this.skillLevelRestrictions = skillLevelRestrictions;
    }
}
