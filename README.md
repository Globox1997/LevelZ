# LevelZ

LevelZ is a mod which adds skillables to the player which can get skilled to unlock blocks and strengthen passive
skills.

### Installation

LevelZ is a mod built for the [Fabric Loader](https://fabricmc.net/). It
requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
and [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) to be installed separately; all other
dependencies are installed with the mod.

### License

LevelZ is licensed under GPLv3.

### Datapacks

Most of the mods default settings can get changed via datapacks, Player attribute affecting settings can get changed via
the config file.\
If you don't know how to create a datapack check out [Data Pack Wiki](https://minecraft.fandom.com/wiki/Data_Pack)
website and try to create your first one for the vanilla game.\
If you know how to create one, the folder path has to be ```data\levelz\FOLDER\YOURFILE.json```\
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

The mod has 5 different folder for different locking things.

1. block
2. brewing
3. entity
4. item
5. mining

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
"minecraft:anvil","minecraft:barrel","minecraft:beacon","minecraft:beehive","minecraft:blast_furnace","minecraft:brewing_stand"
,"minecraft:
cartography","minecraft:cauldron","minecraft:composter","minecraft:enchanting_table","minecraft:grindstone","minecraft:
lectern","minecraft:loom","minecraft:pumpkin","minecraft:smithing_table","minecraft:smoker","minecraft:stonecutter"

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

#### 2. Brewing

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
the level. A list of the brewing items can be found here [Brewing Wiki](https://minecraft.fandom.com/wiki/Brewing). It
is hardcoded to the alchemy skill.

#### 3. Entity

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
"minecraft:cow","minecraft:mooshroom","minecraft:sheep","minecraft:snow_golem","minecraft:villager","minecraft:
wandering_trader"

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

#### 4. Item

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
"minecraft:bow","minecraft:bucket,"minecraft:crossbow","minecraft:dragon_breath","minecraft:elytra","minecraft:
fishing_rod","minecraft:flint_and_steel","minecraft:shield","minecraft:totem_of_undying"

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

Since version 1.1.0: Custom item restrictions got added:

```
{
    "replace": false,
    "skill": "smithing",
    "level": 10,
    "item": "minecraft:custom_item",
    "object": "modid:itemid"
}
```

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

Check out the exampleDatapack folder for an example how it can look like.\
Every level is set to 0 in this pack.