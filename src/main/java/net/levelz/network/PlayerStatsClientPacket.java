package net.levelz.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.data.LevelLoader;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

public class PlayerStatsClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.XP_PACKET, (client, handler, buf, sender) -> {
            if (client.player != null) {
                executeXPPacket(client.player, buf);
            } else {
                PacketByteBuf newBuffer = new PacketByteBuf(Unpooled.buffer());
                newBuffer.writeFloat(buf.readFloat());
                newBuffer.writeInt(buf.readInt());
                newBuffer.writeInt(buf.readInt());
                client.execute(() -> {
                    executeXPPacket(client.player, newBuffer);
                });
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.LEVEL_PACKET, (client, handler, buf, sender) -> {
            if (client.player != null) {
                executeLevelPacket(client.player, buf);
            } else {
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
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.LIST_PACKET, (client, handler, buf, sender) -> {
            if (client.player != null) {
                executeListPacket(buf, client.player);
            } else {
                PacketByteBuf newBuffer = new PacketByteBuf(Unpooled.buffer());
                while (buf.isReadable()) {
                    newBuffer.writeString(buf.readString());
                }
                client.execute(() -> {
                    executeListPacket(newBuffer, client.player);
                });
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.STRENGTH_PACKET, (client, handler, buf, sender) -> {
            double damageAttribute = buf.readDouble();
            client.execute(() -> {
                client.player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(damageAttribute);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.RESET_PACKET, (client, handler, buf, sender) -> {
            if (client.player != null) {
                String skill = buf.readString();
                PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) client.player).getPlayerStatsManager(client.player);
                playerStatsManager.setLevel("points", playerStatsManager.getLevel("points") + playerStatsManager.getLevel(skill));
                playerStatsManager.setLevel(skill, 0);
                // Sync attributes on client
                if (skill.equals("health")) {
                    client.player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                            .setBaseValue(ConfigInit.CONFIG.healthBase + (double) playerStatsManager.getLevel("health") * ConfigInit.CONFIG.healthBonus);
                    client.player.setHealth(client.player.getMaxHealth());
                } else if (skill.equals("strength")) {
                    client.player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                            .setBaseValue(ConfigInit.CONFIG.attackBase + (double) playerStatsManager.getLevel("strength") * ConfigInit.CONFIG.attackBonus);
                } else if (skill.equals("agility")) {
                    client.player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                            .setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerStatsManager.getLevel("agility") * ConfigInit.CONFIG.movementBonus);
                } else if (skill.equals("defense")) {
                    client.player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
                            .setBaseValue(ConfigInit.CONFIG.defenseBase + (double) playerStatsManager.getLevel("defense") * ConfigInit.CONFIG.defenseBonus);
                } else if (skill.equals("luck")) {
                    client.player.getAttributeInstance(EntityAttributes.GENERIC_LUCK)
                            .setBaseValue(ConfigInit.CONFIG.luckBase + (double) playerStatsManager.getLevel("luck") * ConfigInit.CONFIG.luckBonus);
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsServerPacket.LEVEL_EXPERIENCE_ORB_PACKET, (client, handler, buf, sender) -> {

            // NetworkThreadUtils.forceMainThread(packet, this, this.client);
            int id = buf.readVarInt();
            double d = buf.readDouble();
            double e = buf.readDouble();
            double f = buf.readDouble();
            int experienceAmount = buf.readShort();

            // EntityType<?> type = Registry.ENTITY_TYPE.get(buffer.readVarInt());
            // UUID entityUUID = buffer.readUuid();
            // int entityID = buffer.readVarInt();
            // double x = buffer.readDouble();
            // double y = buffer.readDouble();
            // double z = buffer.readDouble();
            // float pitch = (buffer.readByte() * 360) / 256.0F;
            // float yaw = (buffer.readByte() * 360) / 256.0F;
            client.execute(() -> {
                LevelExperienceOrbEntity levelExperienceOrbEntity = new LevelExperienceOrbEntity(client.world, d, e, f, experienceAmount);
                if (levelExperienceOrbEntity != null) {
                    levelExperienceOrbEntity.updateTrackedPosition(d, e, f);
                    levelExperienceOrbEntity.setYaw(0.0f);
                    levelExperienceOrbEntity.setPitch(0.0f);
                    levelExperienceOrbEntity.setId(id);
                    client.world.addEntity(id, levelExperienceOrbEntity);
                }
                // World world = client.player.getEntityWorld();
                // Entity entity = type.create(world);
                // if (entity != null) {
                // entity.updatePosition(x, y, z);
                // entity.updateTrackedPosition(x, y, z);
                // entity.setPitch(pitch);
                // entity.setYaw(yaw);
                // entity.setId(entityID);
                // entity.setUuid(entityUUID);
                // ClientWorld clientWorld = MinecraftClient.getInstance().world;
                // clientWorld.addEntity(entityID, entity);
                // }
            });
        });
    }

    public static void writeC2SIncreaseLevelPacket(PlayerStatsManager playerStatsManager, String string) {
        playerStatsManager.setLevel(string, playerStatsManager.getLevel(string) + 1);
        playerStatsManager.setLevel("points", playerStatsManager.getLevel("points") - 1);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(string);
        buf.writeInt(playerStatsManager.getLevel(string));

        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(PlayerStatsServerPacket.STATS_INCREASE_PACKET, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }

    private static void executeXPPacket(PlayerEntity player, PacketByteBuf buf) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        playerStatsManager.levelProgress = buf.readFloat();
        playerStatsManager.totalLevelExperience = buf.readInt();
        playerStatsManager.setLevel("level", buf.readInt());
    }

    private static void executeLevelPacket(PlayerEntity player, PacketByteBuf buf) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        playerStatsManager.levelProgress = buf.readFloat();
        playerStatsManager.totalLevelExperience = buf.readInt();
        playerStatsManager.setLevel("level", buf.readInt());
        playerStatsManager.setLevel("points", buf.readInt());
        playerStatsManager.setLevel("health", buf.readInt());
        playerStatsManager.setLevel("strength", buf.readInt());
        playerStatsManager.setLevel("agility", buf.readInt());
        playerStatsManager.setLevel("defense", buf.readInt());
        playerStatsManager.setLevel("stamina", buf.readInt());
        playerStatsManager.setLevel("luck", buf.readInt());
        playerStatsManager.setLevel("archery", buf.readInt());
        playerStatsManager.setLevel("trade", buf.readInt());
        playerStatsManager.setLevel("smithing", buf.readInt());
        playerStatsManager.setLevel("mining", buf.readInt());
        playerStatsManager.setLevel("farming", buf.readInt());
        playerStatsManager.setLevel("alchemy", buf.readInt());
        // Set unlocked list
        PlayerStatsServerPacket.syncLockedBlockList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedBrewingItemList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedSmithingItemList(playerStatsManager);
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
                    if (list.get(u).equals("smithing:level"))
                        break;
                    smithingItemList.add(Integer.parseInt(list.get(u)));
                }
                LevelLists.smithingItemList.add(smithingItemList);
            }
        }
        LevelLists.listOfAllLists.clear();
        LevelLoader.addAllInOneList();
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(ConfigInit.CONFIG.healthBase + (double) playerStatsManager.getLevel("health") * ConfigInit.CONFIG.healthBonus);
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerStatsManager.getLevel("agility") * ConfigInit.CONFIG.movementBonus);
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .setBaseValue(ConfigInit.CONFIG.attackBase + (double) playerStatsManager.getLevel("strength") * ConfigInit.CONFIG.attackBonus);
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(ConfigInit.CONFIG.defenseBase + (double) playerStatsManager.getLevel("defense") * ConfigInit.CONFIG.defenseBonus);
        player.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(ConfigInit.CONFIG.luckBase + (double) playerStatsManager.getLevel("luck") * ConfigInit.CONFIG.luckBonus);
        PlayerStatsServerPacket.syncLockedBlockList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedBrewingItemList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedSmithingItemList(playerStatsManager);
    }

    private static void addToList(String listName, String object) {
        if (object.matches("-?(0|[1-9]\\d*)")) {
            LevelLists.getList(listName).add(Integer.parseInt(object));
        } else if (object.equals("false") || object.equals("true")) {
            LevelLists.getList(listName).add(Boolean.parseBoolean(object));
        } else
            LevelLists.getList(listName).add(object);
    }

}