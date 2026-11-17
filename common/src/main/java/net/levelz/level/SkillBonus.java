package net.levelz.level;

import java.util.List;

public class SkillBonus {

    public static final List<String> BONUS_KEYS =
            List.of("bowDamage", "bowDoubleDamageChance", "crossbowDamage", "crossbowDoubleDamageChance", "itemDamageChance", "potionEffectChance", "breedTwinChance",
                    "fallDamageReduction", "deathGraceChance", "tntStrength", "priceDiscount", "tradeXp", "merchantImmune", "miningDropChance", "plantDropChance",
                    "anvilXpCap", "anvilXpDiscount", "anvilXpChance", "healthRegen", "healthAbsorption", "exhaustionReduction", "meleeKnockbackAttackChance", "meleeCriticalAttackChance",
                    "meleeCriticalAttackDamage", "meleeDoubleAttackDamageChance", "foodIncreasion", "damageReflection", "damageReflectionChance", "evadingDamageChance");

    // bowDamage: Each level grants +bowDamage on arrow damage
    // bowDoubleDamageChance: Chance to double arrow damage with bow
    // crossbowDamage: Each level grants +crossbowDamage on arrow damage
    // crossbowDoubleDamageChance: Chance to double arrow damage with crossbow
    // itemDamageChance: Each level grants +chance to not consume item damage on item usage
    // potionEffectChance: Chance to increase effect amplifier by one
    // breedTwinChance: Chance to have twins on breeding
    // fallDamageReduction: Each level grants +fallDamageReduction
    // deathGraceChance: Chance to not die on critical damage intake
    // tntStrength: Grants +tntStrength tnt strength
    // priceDiscount: Each level grants %priceDiscount on trading
    // tradeXp: Each level grants more %tradeXp
    // merchantImmune: Grants immunity to reputation decrease and attack call on damaging merchant
    // miningDropChance: Each level grants %chance to double ore drop
    // plantDropChance: Each level grants %chance to double plant drop
    // anvilXpCap: Grants xp cap on anvil usage
    // anvilXpDiscount: Each level grants %discount on anvil usage
    // anvilXpChance: Chance to not use xp on anvil usage
    // healthRegen: Each level grants %health on regeneration
    // healthAbsorption: Grants absorption on regeneration
    // exhaustionReduction: Each level grants %exhaust reduction
    // meleeKnockbackAttackChance: Each level grants %chance to knockback
    // meleeCriticalAttackChance: Each level grants %chance to critical hit
    // meleeCriticalAttackDamage: Each level grants +critical melee damage on critical hit
    // meleeDoubleAttackDamageChance: Chance to double melee damage
    // foodIncreasion: Each level grants %food value when eating food
    // damageReflection: Each level grants %damage reflection
    // damageReflectionChance: Each level grants %chance to reflect damage
    // evadingDamageChance: Chance to evade incoming damage

    private final String key;
    private final int id;
    private final int level;

    public SkillBonus(String key, int id, int level) {
        this.id = id;
        this.level = level;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

}
