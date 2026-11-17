package net.levelz.level;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PlayerSkill {

    private final int id;
    private int level;

    public PlayerSkill(int id, int level) {
        this.id = id;
        this.level = level;
    }

    public PlayerSkill(ValueInput nbt) {
        this.id = nbt.getIntOr("Id", 0);
        this.level = nbt.getIntOr("Level", 0);
    }

    public void writeDataToNbt(ValueOutput nbt) {
        nbt.putInt("Id", this.id);
        nbt.putInt("Level", this.level);
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void increaseLevel(int level) {
        int maxLevel = LevelManager.SKILLS.get(this.id).getMaxLevel();
        if ((this.level + level) <= maxLevel) {
            this.level += level;
        } else {
            this.level = maxLevel;
        }
    }

    public void decreaseLevel(int level) {
        if ((this.level - level) >= 0) {
            this.level -= level;
        } else {
            this.level = 0;
        }
    }

}
