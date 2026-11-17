package net.levelz.level.restriction;

import java.util.Map;

public class PlayerRestriction {

    private final int id;
    private final Map<Integer, Integer> skillLevelRestrictions; // skillid, lvl

    public PlayerRestriction(int id, Map<Integer, Integer> skillLevelRestrictions) {
        this.id = id;
        this.skillLevelRestrictions = skillLevelRestrictions;
    }

    public int getId() {
        return id;
    }

    public Map<Integer, Integer> getSkillLevelRestrictions() {
        return skillLevelRestrictions;
    }
}
