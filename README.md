# LevelZ

LevelZ is a mod which adds skillables to the player which can get skilled to unlock blocks and strengthen passive
skills.

### Installation

LevelZ is a mod built for the [Fabric Loader](https://fabricmc.net/). It
requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api), [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) and [LibZ](https://www.curseforge.com/minecraft/mc-mods/libz) to be installed separately; all other dependencies are installed with the mod.

### License

LevelZ is licensed under GPLv3.

### Datapacks

Most of the mods default settings can get changed via datapacks, Player attribute affecting settings can get changed via
the config file.  
If you don't know how to create a datapack check out [Data Pack Wiki](https://minecraft.wiki/w/Data_Pack)
website and try to create your first one for the vanilla game.  
If you know how to create one, the folder path has to be ```data\levelz\FOLDER\YOURFILE.json```  
For the skills put the file in folder `skill`, for restrictions use `restriction`.  
Caution! Make sure you name the files differently than the existing ones.  

Since version 2.0.0, skills can be created and overwritten via datapack.

#### Skill
A skill requires:
- id (integer): determines the location in the skill gui - starting at 0 -> 1,2,3,4
- key (string): determines the name of the skill - translated like "skill.levelz.key" - skill texture must be 16x16px at "assets/levelz/textures/gui/sprites/key.png"
- level (integer): determines the max level in this specific skill
- attributes: 
  - id (integer): optional id - determines the location in the skill gui - attribute texture must be 9x9px at "assets/levelz/textures/gui/sprites/type.png" (no mod id infront of the type)
  - type (string): attribute type (vanilla existing ones can be found below)
  - base (float): optional starting value - can be set to -10000.0 if vanilla default starting value should be used
  - operation (string): determines the mathematical operation how to add "value"
  - value (float): value to add/multiply onto the start value
- bonus:
  - level (integer): determines the level at which it will be unlocked
  - key (string): determines the bonus (all bonuses can be found below)

```json
{
  "health": {
    "replace": false,
    "id": 0,
    "key": "health",
    "level": 20,
    "attributes": [
      {
        "type": "generic.max_health",
        "base": 10,
        "operation": "ADD_VALUE",
        "value": 1
      }
    ],
    "bonus": [
      {
        "level": 5,
        "key": "deathGraceChance"
      }
    ]
  },
  "...": {
  }
}
```

##### Attribute Types
- `generic.armor`
- `generic.armor_toughness`
- `generic.attack_damage`
- `generic.attack_knockback`
- `generic.attack_speed`
- `player.block_break_speed`
- `player.block_interaction_range`
- `generic.burning_time`
- `generic.explosion_knockback_resistance`
- `player.entity_interaction_range`
- `generic.fall_damage_multiplier`
- `generic.flying_speed`
- `generic.follow_range`
- `generic.gravity`
- `generic.jump_strength`
- `generic.knockback_resistance`
- `generic.luck`
- `generic.max_absorption`
- `generic.max_health`
- `player.mining_efficiency`
- `generic.movement_efficiency`
- `generic.movement_speed`
- `generic.oxygen_bonus`
- `generic.safe_fall_distance`
- `generic.scale`
- `player.sneaking_speed`
- `zombie.spawn_reinforcements`
- `generic.step_height`
- `player.submerged_mining_speed`
- `player.sweeping_damage_ratio`
- `generic.water_movement_efficiency`

##### Bonuses
- `bowDamage`: Each level grants +bowDamage on arrow damage
- `bowDoubleDamageChance`: Chance to double arrow damage with bow
- `crossbowDamage`: Each level grants +crossbowDamage on arrow damage
- `crossbowDoubleDamageChance`: Chance to double arrow damage with crossbow
- `itemDamageChance`: Each level grants +chance to not consume item damage on item usage
- `potionEffectChance`: Chance to increase effect amplifier by one
- `breedTwinChance`: Chance to have twins on breeding
- `fallDamageReduction`: Each level grants +fallDamageReduction
- `deathGraceChance`: Chance to not die on critical damage intake
- `tntStrength`: Grants +tntStrength tnt strength
- `priceDiscount`: Each level grants %priceDiscount on trading
- `tradeXp`: Each level grants more %tradeXp
- `merchantImmune`: Grants immunity to reputation decrease and attack call on damaging merchant
- `miningDropChance`: Each level grants %chance to double ore drop
- `plantDropChance`: Each level grants %chance to double plant drop
- `anvilXpCap`: Grants xp cap on anvil usage
- `anvilXpDiscount`: Each level grants %discount on anvil usage
- `anvilXpChance`: Chance to not use xp on anvil usage
- `healthRegen`: Each level grants %health on regeneration
- `healthAbsorption`: Grants absorption on regeneration
- `exhaustionReduction`: Each level grants %exhaust reduction
- `meleeKockbackAttackChance`: Each level grants %chance to knockback
- `meleeCriticalAttackChance`: Each level grants %chance to critical hit
- `meleeCriticalAttackDamage`: Each level grants +critical melee damage on critical hit
- `meleeDoubleAttackDamageChance`: Chance to double melee damage
- `foodIncreasion`: Each level grants %food value when eating food
- `damageReflection`: Each level grants %damage reflection
- `damageReflectionChance`: Each level grants %chance to reflect damage
- `evadingDamageChance`: Chance to evade incoming damage

#### Restriction
A restriction requires:
- skills: one or multiple skills with the respective required level

A restriction can include:
- blocks: restricts usage of the blocks
- crafting: restricts recipes by item output id
- entities: restricts usage of entities
- items: restricts usage of items
- mining: restricts mining of blocks
- enchantments: restricts enchantments

```json
{
  "name_it_as_you_want": {
    "replace": false,
    "skills": {
      "archery": 1,
      "health": 5
    },
    "blocks": [
      "minecraft:anvil"
    ],
    "crafting": [
      "minecraft:oak_planks",
      "minecraft:iron_sword",
      "minecraft:iron_chestplate"
    ],
    "entities": [
      "minecraft:villager"
    ],
    "items": [
      "minecraft:iron_sword"
    ],
    "mining": [
      "minecraft:pumpkin",
      "minecraft:stone",
      "minecraft:chiseled_polished_blackstone"
    ],
    "enchantments": {
      "minecraft:unbreaking": 1
    }
  },
  "...": {
    "skills": {
      "health": 1
    },
    "mining": [
      "minecraft:dirt"
    ]
  }
}
```

#### Information Display
To display information about a skill in the level gui, just add some lines in the lang json with the following key.  
`"skill.levelz.yourskillkey.0": "This is the whatever skill"`,  
`"skill.levelz.yourskillkey.1": "It does somethin"`,  
`...`  
Just increase the integer at the end of the json entry.

#### Disable access to something
To permanently disable access to something, simply set the skill requirement higher than the `maxLevel` property configured in `levelz.json5`.

### Advancement
LevelZ provides two advancement criterions trigger called `levelz:level` and `levelz:skill`.\
The first one triggers when the player reached the set level.

```json
    "criteria": {
        "levelexample": {
            "trigger": "levelz:level",
            "conditions": {
                "level": 100
            }
        }
    }
```

The second one triggers when the player reached the set skill level.

```json
    "criteria": {
        "otherlevelexample": {
            "trigger": "levelz:skill",
             "conditions": {
                "skill_name": "health",
                "skill_level": 20
            }
        }
    }
```

### Additional configuration
Levelz exposes configuration settings for many attributes, such as
setting max levels, experience rates from different mobs, etc..
These configuration settings are found in `${MINECRAFTDIR}/config/levelz.json5`

### Commands
`/level playername add skill integer`
- Increase the specific skill by the integer value
  
`/level playername remove skill integer`
- Decrease the specific skill by the integer value

`/level playername set skill integer`
- Set the specific skill to the integer value

`/level playername get skill`
- Print the specific skill level

### Info
Inside the config there is a developer mode setting, when set to true, inside the creative menu, hover over an item/block with your mouse and press f8 (default key) to create or append the item/block id to the file called idlist.json inside your minecraft folder for easier datapack creation.
