package net.levelz.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.data.LevelLoader;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

public class PlayerStatsClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.STATS_SYNC_PACKET, (client, handler, buf, sender) -> {
            String skillString = buf.readString().toUpperCase();
            int level = buf.readInt();
            int points = buf.readInt();
            client.execute(() -> {
                PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) client.player).getPlayerStatsManager();
                Skill skill = Skill.valueOf(skillString);

                playerStatsManager.setSkillLevel(skill, level);
                playerStatsManager.setSkillPoints(points);

                if (skill == Skill.STRENGTH) {
                    playerStatsManager.getPlayerEntity().getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                            .setBaseValue(ConfigInit.CONFIG.attackBase + (double) playerStatsManager.getSkillLevel(Skill.STRENGTH) * ConfigInit.CONFIG.attackBonus);
                }
                PlayerStatsServerPacket.syncLockedCraftingItemList(playerStatsManager);
                switch (skill) {
                case SMITHING -> PlayerStatsServerPacket.syncLockedSmithingItemList(playerStatsManager);
                case MINING -> PlayerStatsServerPacket.syncLockedBlockList(playerStatsManager);
                case ALCHEMY -> PlayerStatsServerPacket.syncLockedBrewingItemList(playerStatsManager);
                default -> {
                }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.XP_PACKET, (client, handler, buf, sender) -> {
            PacketByteBuf newBuffer = new PacketByteBuf(Unpooled.buffer());
            newBuffer.writeFloat(buf.readFloat());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            client.execute(() -> {
                executeXPPacket(client.player, newBuffer);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.LEVEL_PACKET, (client, handler, buf, sender) -> {
            PacketByteBuf newBuffer = new PacketByteBuf(Unpooled.buffer());
            newBuffer.writeFloat(buf.readFloat());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            newBuffer.writeInt(buf.readInt());
            client.execute(() -> {
                executeLevelPacket(client.player, newBuffer);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.LIST_PACKET, (client, handler, buf, sender) -> {
            PacketByteBuf newBuffer = new PacketByteBuf(Unpooled.buffer());
            while (buf.isReadable()) {
                newBuffer.writeString(buf.readString());
            }
            client.execute(() -> {
                executeListPacket(newBuffer, client.player);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.STRENGTH_PACKET, (client, handler, buf, sender) -> {
            double damageAttribute = buf.readDouble();
            client.execute(() -> {
                client.player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(damageAttribute);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.RESET_PACKET, (client, handler, buf, sender) -> {
            if (client.player != null) {
                String skillString = buf.readString();

                // Sync attributes on client
                client.execute(() -> {
                    Skill skill = Skill.valueOf(skillString.toUpperCase());
                    PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) client.player).getPlayerStatsManager();
                    int skillLevel = playerStatsManager.getSkillLevel(skill);
                    playerStatsManager.setSkillPoints(playerStatsManager.getSkillPoints() + skillLevel);
                    playerStatsManager.setSkillLevel(skill, 0);

                    switch (skill) {
                    case HEALTH -> {
                        client.player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(ConfigInit.CONFIG.healthBase);
                        client.player.setHealth(client.player.getMaxHealth());
                    }
                    case STRENGTH -> client.player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(ConfigInit.CONFIG.attackBase);
                    case AGILITY -> client.player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(ConfigInit.CONFIG.movementBase);
                    case DEFENSE -> client.player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(ConfigInit.CONFIG.defenseBase);
                    case LUCK -> client.player.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(ConfigInit.CONFIG.luckBase);
                    default -> {
                    }
                    }
                });

            }
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.LEVEL_EXPERIENCE_ORB_PACKET, (client, handler, buf, sender) -> {
            int id = buf.readVarInt();
            double d = buf.readDouble();
            double e = buf.readDouble();
            double f = buf.readDouble();
            int experienceAmount = buf.readShort();
            client.execute(() -> {
                LevelExperienceOrbEntity levelExperienceOrbEntity = new LevelExperienceOrbEntity(client.world, d, e, f, experienceAmount);
                if (levelExperienceOrbEntity != null) {
                    levelExperienceOrbEntity.updateTrackedPosition(d, e, f);
                    levelExperienceOrbEntity.setYaw(0.0f);
                    levelExperienceOrbEntity.setPitch(0.0f);
                    levelExperienceOrbEntity.setId(id);
                    client.world.addEntity(id, levelExperienceOrbEntity);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.CONFIG_SYNC_PACKET, (client, handler, buf, sender) -> {
            int maxLevel = buf.readInt();
            int overallMaxLevel = buf.readInt();

            double healthBase = buf.readDouble();
            double healthBonus = buf.readDouble();
            float healthAbsorptionBonus = buf.readFloat();
            double movementBase = buf.readDouble();
            double movementBonus = buf.readDouble();
            float movementMissChance = buf.readFloat();
            float movementFallBonus = buf.readFloat();
            double attackBase = buf.readDouble();
            double attackBonus = buf.readDouble();
            float attackDoubleDamageChance = buf.readFloat();
            float attackCritDmgBonus = buf.readFloat();
            double defenseBase = buf.readDouble();
            double defenseBonus = buf.readDouble();
            float defenseReflectChance = buf.readFloat();
            double luckBase = buf.readDouble();
            double luckBonus = buf.readDouble();

            float luckCritBonus = buf.readFloat();
            float luckSurviveChance = buf.readFloat();
            float staminaBase = buf.readFloat();
            float staminaBonus = buf.readFloat();

            float staminaHealthBonus = buf.readFloat();
            float staminaFoodBonus = buf.readFloat();
            double tradeBonus = buf.readDouble();
            float tradeXPBonus = buf.readFloat();
            boolean tradeReputation = buf.readBoolean();
            float smithingCostBonus = buf.readFloat();
            float smithingToolChance = buf.readFloat();
            float smithingAnvilChance = buf.readFloat();
            int farmingBase = buf.readInt();
            float farmingChanceBonus = buf.readFloat();
            float farmingTwinChance = buf.readFloat();
            float alchemyEnchantmentChance = buf.readFloat();
            float alchemyPotionChance = buf.readFloat();
            float archeryInaccuracyBonus = buf.readFloat();
            float archeryBowExtraDamage = buf.readFloat();
            float archeryCrossbowExtraDamage = buf.readFloat();
            float archeryDoubleDamageChance = buf.readFloat();
            float miningOreChance = buf.readFloat();
            float miningTntBonus = buf.readFloat();
            boolean bindAxeDamageToSwordRestriction = buf.readBoolean();

            boolean useIndependentExp = buf.readBoolean();
            float xpCostMultiplicator = buf.readFloat();
            int xpExponent = buf.readInt();
            int xpBaseCost = buf.readInt();
            int xpMaxCost = buf.readInt();

            boolean miningProgression = buf.readBoolean();
            boolean itemProgression = buf.readBoolean();
            boolean blockProgression = buf.readBoolean();
            boolean entityProgression = buf.readBoolean();
            boolean brewingProgression = buf.readBoolean();
            boolean smithingProgression = buf.readBoolean();

            client.execute(() -> {
                ConfigInit.CONFIG.useIndependentExp = useIndependentExp;
                ConfigInit.CONFIG.maxLevel = maxLevel;
                ConfigInit.CONFIG.overallMaxLevel = overallMaxLevel;
                ConfigInit.CONFIG.xpBaseCost = xpBaseCost;
                ConfigInit.CONFIG.xpMaxCost = xpMaxCost;
                ConfigInit.CONFIG.xpCostMultiplicator = xpCostMultiplicator;
                ConfigInit.CONFIG.xpExponent = xpExponent;

                ConfigInit.CONFIG.healthBase = healthBase;
                ConfigInit.CONFIG.healthBonus = healthBonus;
                ConfigInit.CONFIG.healthAbsorptionBonus = healthAbsorptionBonus;
                ConfigInit.CONFIG.movementBase = movementBase;
                ConfigInit.CONFIG.movementBonus = movementBonus;
                ConfigInit.CONFIG.movementMissChance = movementMissChance;
                ConfigInit.CONFIG.movementFallBonus = movementFallBonus;
                ConfigInit.CONFIG.attackBase = attackBase;
                ConfigInit.CONFIG.attackBonus = attackBonus;
                ConfigInit.CONFIG.attackDoubleDamageChance = attackDoubleDamageChance;
                ConfigInit.CONFIG.attackCritDmgBonus = attackCritDmgBonus;
                ConfigInit.CONFIG.defenseBase = defenseBase;
                ConfigInit.CONFIG.defenseBonus = defenseBonus;
                ConfigInit.CONFIG.defenseReflectChance = defenseReflectChance;
                ConfigInit.CONFIG.luckBase = luckBase;
                ConfigInit.CONFIG.luckBonus = luckBonus;
                ConfigInit.CONFIG.luckCritBonus = luckCritBonus;
                ConfigInit.CONFIG.luckSurviveChance = luckSurviveChance;
                ConfigInit.CONFIG.staminaBase = staminaBase;
                ConfigInit.CONFIG.staminaBonus = staminaBonus;
                ConfigInit.CONFIG.staminaHealthBonus = staminaHealthBonus;
                ConfigInit.CONFIG.staminaFoodBonus = staminaFoodBonus;
                ConfigInit.CONFIG.tradeBonus = tradeBonus;
                ConfigInit.CONFIG.tradeXPBonus = tradeXPBonus;
                ConfigInit.CONFIG.tradeReputation = tradeReputation;
                ConfigInit.CONFIG.smithingCostBonus = smithingCostBonus;
                ConfigInit.CONFIG.smithingToolChance = smithingToolChance;
                ConfigInit.CONFIG.smithingAnvilChance = smithingAnvilChance;
                ConfigInit.CONFIG.farmingBase = farmingBase;
                ConfigInit.CONFIG.farmingChanceBonus = farmingChanceBonus;
                ConfigInit.CONFIG.farmingTwinChance = farmingTwinChance;
                ConfigInit.CONFIG.alchemyEnchantmentChance = alchemyEnchantmentChance;
                ConfigInit.CONFIG.alchemyPotionChance = alchemyPotionChance;
                ConfigInit.CONFIG.archeryInaccuracyBonus = archeryInaccuracyBonus;
                ConfigInit.CONFIG.archeryBowExtraDamage = archeryBowExtraDamage;
                ConfigInit.CONFIG.archeryCrossbowExtraDamage = archeryCrossbowExtraDamage;
                ConfigInit.CONFIG.archeryDoubleDamageChance = archeryDoubleDamageChance;
                ConfigInit.CONFIG.miningOreChance = miningOreChance;
                ConfigInit.CONFIG.miningTntBonus = miningTntBonus;
                ConfigInit.CONFIG.bindAxeDamageToSwordRestriction = bindAxeDamageToSwordRestriction;

                ConfigInit.CONFIG.miningProgression = miningProgression;
                ConfigInit.CONFIG.itemProgression = itemProgression;
                ConfigInit.CONFIG.blockProgression = blockProgression;
                ConfigInit.CONFIG.entityProgression = entityProgression;
                ConfigInit.CONFIG.brewingProgression = brewingProgression;
                ConfigInit.CONFIG.smithingProgression = smithingProgression;
            });

        });
    }

    public static void writeC2SIncreaseLevelPacket(PlayerStatsManager playerStatsManager, Skill skill, int level) {
        int skillLevel = playerStatsManager.getSkillLevel(skill);
        level = Math.min(playerStatsManager.getSkillPoints(), level);
        level = Math.min(ConfigInit.CONFIG.maxLevel - skillLevel, level);
        if (level < 1) {
            return;
        }

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(skill.name());
        buf.writeInt(level);

        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(PlayerStatsServerPacket.STATS_INCREASE_PACKET, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }

    public static void writeC2SSyncConfigPacket() {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(PlayerStatsServerPacket.SEND_CONFIG_SYNC_PACKET, new PacketByteBuf(Unpooled.buffer())));
    }

    // public static void writeC2STagPacket(List<Object> list) {
    // PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    // CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(PlayerStatsServerPacket.SEND_TAG_PACKET, buf);
    // MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    // }

    // Private executes
    private static void executeXPPacket(PlayerEntity player, PacketByteBuf buf) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
        playerStatsManager.setLevelProgress(buf.readFloat());
        playerStatsManager.setTotalLevelExperience(buf.readInt());
        playerStatsManager.setOverallLevel(buf.readInt());
        // playerStatsManager.setLevel("level", buf.readInt());
    }

    private static void executeLevelPacket(PlayerEntity player, PacketByteBuf buf) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
        playerStatsManager.setLevelProgress(buf.readFloat());
        playerStatsManager.setTotalLevelExperience(buf.readInt());
        playerStatsManager.setOverallLevel(buf.readInt());
        playerStatsManager.setSkillPoints(buf.readInt());
        for (Skill skill : Skill.values()) {
            playerStatsManager.setSkillLevel(skill, buf.readInt());
        }
        // Set unlocked list
        PlayerStatsServerPacket.syncLockedBlockList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedBrewingItemList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedSmithingItemList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedCraftingItemList(playerStatsManager);
    }

    private static void executeListPacket(PacketByteBuf buf, ClientPlayerEntity player) {
        LevelLoader.clearEveryList();
        ArrayList<String> list = new ArrayList<>();
        while (buf.isReadable()) {
            list.add(buf.readString());
        }
        for (int i = 0; i < list.size(); i++) {
            String listName = list.get(i).toString();
            if (LevelLists.getListNames().contains(listName)) {
                int count = 2;
                int negativeCount = -2;
                if (listName.equals("minecraft:armor") || listName.equals("minecraft:tool") || listName.equals("minecraft:hoe") || listName.equals("minecraft:sword")
                        || listName.equals("minecraft:axe") || listName.equals("minecraft:custom_block") || listName.equals("minecraft:custom_item") || listName.equals("minecraft:custom_entity"))
                    negativeCount--;
                if (listName.equals("minecraft:enchanting_table")) {
                    count = 5;
                }
                for (int u = negativeCount; u < count; u++) {
                    addToList(listName, list.get(i + u));
                }
            } else if (listName.equals("mining:level")) {
                List<Integer> blockList = new ArrayList<>();
                LevelLists.miningLevelList.add(Integer.parseInt(list.get(i + 1)));
                for (int u = i + 2; u < list.size(); u++) {
                    if (list.get(u).equals("mining:level") || list.get(u).equals("brewing:level"))
                        break;
                    blockList.add(Integer.parseInt(list.get(u)));
                }
                LevelLists.miningBlockList.add(blockList);
            } else if (listName.equals("brewing:level")) {
                List<Integer> brewingItemList = new ArrayList<>();
                LevelLists.brewingLevelList.add(Integer.parseInt(list.get(i + 1)));
                for (int u = i + 2; u < list.size(); u++) {
                    if (list.get(u).equals("brewing:level") || list.get(u).equals("smithing:level"))
                        break;
                    brewingItemList.add(Integer.parseInt(list.get(u)));
                }
                LevelLists.brewingItemList.add(brewingItemList);
            } else if (listName.equals("smithing:level")) {
                List<Integer> smithingItemList = new ArrayList<>();
                LevelLists.smithingLevelList.add(Integer.parseInt(list.get(i + 1)));
                for (int u = i + 2; u < list.size(); u++) {
                    if (list.get(u).equals("smithing:level") || list.get(u).equals("crafting:level"))
                        break;
                    smithingItemList.add(Integer.parseInt(list.get(u)));
                }
                LevelLists.smithingItemList.add(smithingItemList);
            } else if (listName.equals("crafting:level")) {
                List<Integer> craftingItemList = new ArrayList<>();
                LevelLists.craftingLevelList.add(Integer.parseInt(list.get(i + 1)));
                LevelLists.craftingSkillList.add(String.valueOf(list.get(i + 2)));
                for (int u = i + 3; u < list.size(); u++) {
                    if (list.get(u).equals("crafting:level"))
                        break;
                    craftingItemList.add(Integer.parseInt(list.get(u)));
                }
                LevelLists.craftingItemList.add(craftingItemList);
            }
        }
        LevelLists.listOfAllLists.clear();
        LevelLoader.addAllInOneList();
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .setBaseValue(ConfigInit.CONFIG.healthBase + (double) playerStatsManager.getSkillLevel(Skill.HEALTH) * ConfigInit.CONFIG.healthBonus);
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerStatsManager.getSkillLevel(Skill.AGILITY) * ConfigInit.CONFIG.movementBonus);
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .setBaseValue(ConfigInit.CONFIG.attackBase + (double) playerStatsManager.getSkillLevel(Skill.STRENGTH) * ConfigInit.CONFIG.attackBonus);
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
                .setBaseValue(ConfigInit.CONFIG.defenseBase + (double) playerStatsManager.getSkillLevel(Skill.DEFENSE) * ConfigInit.CONFIG.defenseBonus);
        player.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(ConfigInit.CONFIG.luckBase + (double) playerStatsManager.getSkillLevel(Skill.LUCK) * ConfigInit.CONFIG.luckBonus);
        PlayerStatsServerPacket.syncLockedBlockList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedBrewingItemList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedSmithingItemList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedCraftingItemList(playerStatsManager);
    }

    private static void addToList(String listName, String object) {
        if (object.matches("-?(0|[1-9]\\d*)")) {
            LevelLists.getList(listName).add(Integer.parseInt(object));
        } else if (object.equals("false") || object.equals("true")) {
            LevelLists.getList(listName).add(Boolean.parseBoolean(object));
        } else
            LevelLists.getList(listName).add(object);
    }

    public static void writeC2SLevelUpPacket(int levels) {
        // Levelz level up
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(levels);
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(PlayerStatsServerPacket.LEVEL_UP_BUTTON_PACKET, buf);
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(packet);
    }

}