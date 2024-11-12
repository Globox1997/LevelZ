package net.levelz.level;

import net.minecraft.text.Text;

import java.util.List;

public class Skill {

    private final int id;
    private final String key;
    private final int maxLevel;
    private final List<SkillAttribute> attributes;

    public Skill(int id, String key, int maxLevel, List<SkillAttribute> attributes) {
        this.id = id;
        this.key = key;
        this.maxLevel = maxLevel;
        this.attributes = attributes;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<SkillAttribute> getAttributes() {
        return attributes;
    }

    public Text getText() {
        return Text.translatable("skill.levelz." + key);
    }

}
