package net.levelz.level;

import net.minecraft.nbt.CompoundTag;

public class PlayerSkill {

    private final int id;
    private int level;

    public PlayerSkill(int id, int level) {
        this.id = id;
        this.level = level;
    }

    public PlayerSkill(CompoundTag nbt) {
        this.id = nbt.getInt("Id");
        this.level = nbt.getInt("Level");
    }

    public CompoundTag writeDataToNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Id", this.id);
        nbt.putInt("Level", this.level);
        return nbt;
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
