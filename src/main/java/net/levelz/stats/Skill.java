package net.levelz.stats;

import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;

public class Skill implements Comparable<Skill>{
    private static final ArrayList<Skill> registeredSkills = new ArrayList<>();

    public final static Skill HEALTH = new Skill(1, "HealthLevel", "HEALTH");
    public final static Skill STRENGTH = new Skill(2, "StrengthLevel", "STRENGTH");
    public final static Skill AGILITY = new Skill(3, "AgilityLevel", "AGILITY");
    public final static Skill DEFENSE = new Skill(4, "DefenseLevel", "DEFENSE");
    public final static Skill STAMINA = new Skill(5, "StaminaLevel", "STAMINA");
    public final static Skill LUCK = new Skill(6, "LuckLevel", "LUCK");
    public final static Skill ARCHERY = new Skill(7, "ArcheryLevel", "ARCHERY");
    public final static Skill TRADE = new Skill(8, "TradeLevel", "TRADE");
    public final static Skill SMITHING = new Skill(9, "SmithingLevel", "SMITHING");
    public final static Skill MINING = new Skill(10, "MiningLevel", "MINING");
    public final static Skill FARMING = new Skill(11, "FarmingLevel", "FARMING");
    public final static Skill ALCHEMY = new Skill(12, "AlchemyLevel", "ALCHEMY");

    final int id;
    final String nbt;
    final String name;

    Skill(int id, String nbt, String name) {
        this.id = id;
        this.nbt = nbt;
        this.name = name;
        registeredSkills.add(this);
    }

    public static Skill[] values() {
        return registeredSkills.toArray(Skill[]::new);
    }

    public static Skill register(String nbt, String name){
        return new Skill(registeredSkills.get(registeredSkills.size() - 1).id + 1, nbt, name);
    }

    public static Skill fromNbt(String nbt) {
        for(Skill skill : registeredSkills) {
            if(skill.nbt.equals(nbt)) {
                return skill;
            }
        }
        return null;
    }

    public static Skill valueOf(String name){
        name = name.toUpperCase();
        for(Skill skill : registeredSkills) {
            if(skill.name.equals(name)) {
                return skill;
            }
        }
        return null;
    }

    public static Iterable<Skill> listInRandomOrder(Random random) {
        if (random == null) {
            random = Random.create();
        }
        Random finalRandom = random;
        ArrayList<Skill> randomSkills = new ArrayList<>(registeredSkills);
        randomSkills.sort(Comparator.comparingInt((skill) -> finalRandom.nextInt()));
        return randomSkills;
    }

    public static int getSkillCount() {
        return registeredSkills.size();
    }

    public int getId() {
        return id;
    }

    public String getNbt() {
        return nbt;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NotNull Skill skill) {
        return Integer.compare(id, skill.id);
    }
}
