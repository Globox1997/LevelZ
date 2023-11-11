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
the config file.\
If you don't know how to create a datapack check out [Data Pack Wiki](https://minecraft.wiki/w/Data_Pack)
website and try to create your first one for the vanilla game.\
If you know how to create one, the folder path has to be ```data\levelz\FOLDER\YOURFILE.json```\
Caution! Make sure you name the files differently than the existing ones.\
Each locking file can choose its depending skill except for the mining and brewing list. A list of the skills can be
found here and are self-explanatory:

* health
* strength
* agility
* defense
* stamina
* luck
* archery
* trade
* smithing
* mining
* farming
* alchemy

The mod has 6 different folder for different locking things.

1. block
2. entity
3. item
4. brewing
5. mining
6. smithing
7. crafting

#### 1. Block

The block category stands for the use of a few vanilla blocks. An example how the json file should look like is here:

```
{
    "replace": false,
    "skill": "smithing",
    "level": 10,
    "block": "minecraft:anvil"
}
```

"replace": has to be "true" if you want to overwrite the default setting.\
"skill": has to be one of the mentioned skills.\
"level": is the level when the player unlocks the interaction of the block.\
"block": has to be one of the following strings:\
``
"minecraft:anvil","minecraft:barrel","minecraft:beacon","minecraft:beehive","minecraft:blast_furnace","minecraft:brewing_stand",
"minecraft:cartography_table","minecraft:cauldron","minecraft:composter","minecraft:enchanting_table","minecraft:grindstone","minecraft:lectern",
"minecraft:loom","minecraft:pumpkin","minecraft:smithing_table","minecraft:smoker","minecraft:stonecutter","minecraft:fletching_table","minecraft:crafting_table"
``

It will only cancel the interaction with the block.\
Since version 1.1.0: Custom block restrictions got added:

```
{
    "replace": false,
    "skill": "smithing",
    "level": 10,
    "block": "minecraft:custom_block",
    "object": "modid:blockid"
}
```

One last thing to mention: the enchanting table list takes a unlocking level list inside its json file which looks like
this:

```
    "enchanting": [
        5,
        10,
        18
    ]
```

#### 2. Entity

The entity category is pretty similar to the block category and will lock the interaction with the set entity. An
example how the json file should look like is here:

```
{
    "replace": false,
    "skill": "farming",
    "level": 7,
    "entity": "minecraft:cow"
}
```

"entity": has to be one of the following strings:\
``
"minecraft:cow","minecraft:mooshroom","minecraft:sheep","minecraft:snow_golem","minecraft:villager","minecraft:wandering_trader",
"minecraft:axolotl","minecraft:piglin","minecraft:wolf","minecraft:tadpole","minecraft:allay","minecraft:goat"
``

It will only cancel the interaction with the entity.

Since version 1.1.0: Custom entity restrictions got added:

```
{
    "replace": false,
    "skill": "smithing",
    "level": 10,
    "entity": "minecraft:custom_entity",
    "object": "modid:entityid"
}
```

#### 3. Item

The item category stands for the use of items. An example how the json file should look like is here:

```
{
    "replace": false,
    "skill": "archery",
    "level": 10,
    "item": "minecraft:crossbow"
}
```

"item": has to be one of the following strings:\
``
"minecraft:bow","minecraft:bucket,"minecraft:crossbow","minecraft:dragon_breath","minecraft:elytra","minecraft:fishing_rod",
"minecraft:flint_and_steel","minecraft:shield","minecraft:totem_of_undying","minecraft:shears","minecraft:compass","minecraft:trident"
``

The item category also has 4 special categories:

* armor
* axe
* sword
* tool

An example of one of those categories is here:

```
{
    "replace": false,
    "skill": "defense",
    "level": 8,
    "item": "minecraft:armor",
    "material": "iron"
}
```

It takes a fifth argument called "material" to determine which material of items will be locked.\
Each of these items has to be in the corresponding fabric item tag.

Since version 1.1.0: Custom item restrictions got added:\
Use it only if those two previous options don't work!

```
{
    "replace": false,
    "skill": "smithing",
    "level": 10,
    "item": "minecraft:custom_item",
    "object": "modid:itemid"
}
```

#### 4. Brewing

The brewing category stands for the use of brewing ingredient items at the brewing table. An example how the json file
should look like is here:

```
{
    "replace": false,
    "level": 4,
    "item": [
        "minecraft:golden_carrot"
    ]
}
```

It looks the slots of the brewing table, so players can't insert items to brew the resulting potion before they reached
the level. A list of the brewing items can be found here [Brewing Wiki](https://minecraft.wiki/w/Brewing). It
is hardcoded to the alchemy skill.

#### 5. Mining

The mining category stands for the locked block drop. An example how the json file should look like is here:

```
{
    "replace": false,
    "level": 5,
    "block": [
        "minecraft:coal_ore",
        "minecraft:coal_block",
        "minecraft:deepslate_coal_ore"
    ]
}
```

The "block" list has to have the id of the locked blocks.\
The player can still break those blocks but it won't drop anything and the blockbreaking process is 50% slower (by
default).

#### 6. Smithing (Since version 1.1.5)

The smithing category stands for the crafting restriction of items at the smithing table. An example how the json file
should look like is here:

```
{
    "replace": false,
    "level": 20,
    "item": [
        "minecraft:netherite_sword",
        "minecraft:netherite_pickaxe"
    ]
}
```

It locks the crafting recipe of the smithing table, so players can't pull out crafting result items of the table before they reached
the level. It is hardcoded to the smithing skill.

#### 7. Crafting (Since version 1.3.0)

The crafting category stands for the crafting restriction of items at the crafting table and player gui. An example how the json file
should look like is here:

```
{
    "replace": false,
    "level": 4,
    "skill": "luck",
    "item": [
        "minecraft:stick",
        "minecraft:wooden_axe"
    ]
}
```

#### Disable access to entities
To permanently disable access to an entity, simply set the skill requirement higher than the `maxLevel` property configured in `levelz.json5`.

#### Full list of available entities
See https://github.com/Globox1997/LevelZ/tree/1.19/src/main/resources/data/levelz

### Example Datapack

Check out the exampleDatapack folder for an example how it can look like.\
Every level is set to 0 in this pack.

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
`/playerstats playername add skill integer`
- Increase the specific skill by the integer value
  
`/playerstats playername remove skill integer`
- Decrease the specific skill by the integer value

`/playerstats playername set skill integer`
- Set the specific skill to the integer value

`/playerstats playername get skill`
- Print the specific skill level

`/info material`
- Print the material string of the item in hand

### Info
Inside the config there is a developer mode setting, when set to true, inside the creative menu, hover over an item/block with your mouse and press f8 (default key) to create or append the item/block id to the file called idlist.json inside your minecraft folder for easier datapack creation.
