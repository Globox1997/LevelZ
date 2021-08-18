package net.levelz.network;

import java.util.ArrayList;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerStatsServerPacket {

    public static final Identifier STATS_INCREASE_PACKET = new Identifier("levelz", "player_increase_stats");
    public static final Identifier XP_PACKET = new Identifier("levelz", "player_level_xp");
    public static final Identifier LEVEL_PACKET = new Identifier("levelz", "player_level_stats");
    public static final Identifier LIST_PACKET = new Identifier("levelz", "unlocking_list");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(STATS_INCREASE_PACKET, (server, player, handler, buffer, sender) -> {
            if (player != null) {
                String stat = buffer.readString();
                PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
                playerStatsManager.setLevel(stat, buffer.readInt());
                playerStatsManager.setLevel("points", playerStatsManager.getLevel("points") - 1);
                if (stat.equals("health")) {
                    player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) + 1D);
                    player.setHealth(player.getHealth() + 1F);
                } else if (stat.equals("strength")) {
                    player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + 0.2D);
                } else if (stat.equals("agility")) {
                    player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + 0.001D);
                } else if (stat.equals("defense")) {
                    player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR) + 0.2D);
                } else if (stat.equals("luck")) {
                    player.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR) + 0.05D);
                } else if (stat.equals("mining")) {
                    syncLockedBlockList(playerStatsManager);
                } else if (stat.equals("alchemy")) {
                    syncLockedBrewingItemList(playerStatsManager);
                }
            }
        });

    }

    public static void writeS2CXPPacket(PlayerStatsManager playerStatsManager, ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeFloat(playerStatsManager.levelProgress);
        buf.writeInt(playerStatsManager.totalLevelExperience);
        buf.writeInt(playerStatsManager.getLevel("level"));
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(XP_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CSkillPacket(PlayerStatsManager playerStatsManager, ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeFloat(playerStatsManager.levelProgress);
        buf.writeInt(playerStatsManager.totalLevelExperience);
        buf.writeInt(playerStatsManager.getLevel("level"));
        buf.writeInt(playerStatsManager.getLevel("points"));
        buf.writeInt(playerStatsManager.getLevel("health"));
        buf.writeInt(playerStatsManager.getLevel("strength"));
        buf.writeInt(playerStatsManager.getLevel("agility"));
        buf.writeInt(playerStatsManager.getLevel("defense"));
        buf.writeInt(playerStatsManager.getLevel("stamina"));
        buf.writeInt(playerStatsManager.getLevel("luck"));
        buf.writeInt(playerStatsManager.getLevel("archery"));
        buf.writeInt(playerStatsManager.getLevel("trade"));
        buf.writeInt(playerStatsManager.getLevel("smithing"));
        buf.writeInt(playerStatsManager.getLevel("mining"));
        buf.writeInt(playerStatsManager.getLevel("farming"));
        buf.writeInt(playerStatsManager.getLevel("alchemy"));

        // Set on server
        syncLockedBlockList(playerStatsManager);
        syncLockedBrewingItemList(playerStatsManager);

        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(LEVEL_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void syncLockedBlockList(PlayerStatsManager playerStatsManager) {
        playerStatsManager.lockedBlockIds.clear();
        for (int i = 0; i < LevelLists.miningLevelList.size(); i++) {
            if (LevelLists.miningLevelList.get(i) > playerStatsManager.getLevel("mining")) {
                for (int u = 0; u < LevelLists.miningBlockList.get(i).size(); u++) {
                    if (!playerStatsManager.lockedBlockIds.contains(LevelLists.miningBlockList.get(i).get(u))) {
                        playerStatsManager.lockedBlockIds.add(LevelLists.miningBlockList.get(i).get(u));
                    }
                }
            }
        }
    }

    public static void syncLockedBrewingItemList(PlayerStatsManager playerStatsManager) {
        playerStatsManager.lockedbrewingItemIds.clear();
        for (int i = 0; i < LevelLists.brewingLevelList.size(); i++) {
            if (LevelLists.brewingLevelList.get(i) > playerStatsManager.getLevel("alchemy")) {
                for (int u = 0; u < LevelLists.brewingItemList.get(i).size(); u++) {
                    if (!playerStatsManager.lockedbrewingItemIds.contains(LevelLists.brewingItemList.get(i).get(u))) {
                        playerStatsManager.lockedbrewingItemIds.add(LevelLists.brewingItemList.get(i).get(u));
                    }
                }
            }
        }
    }

    public static void writeS2CListPacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        for (int i = 0; i < LevelLists.getListNames().size(); i++) {
            String listName = LevelLists.getListNames().get(i);
            ArrayList<Object> list = LevelLists.getList(listName);
            for (int u = 0; u < list.size(); u++) {
                buf.writeString(list.get(u).toString());
            }
        }
        for (int k = 0; k < LevelLists.miningLevelList.size(); k++) {
            buf.writeString("mining:level");
            buf.writeString(LevelLists.miningLevelList.get(k).toString());
            for (int u = 0; u < LevelLists.miningBlockList.get(k).size(); u++) {
                buf.writeString(LevelLists.miningBlockList.get(k).get(u).toString());
            }
        }
        for (int k = 0; k < LevelLists.brewingLevelList.size(); k++) {
            buf.writeString("brewing:level");
            buf.writeString(LevelLists.brewingLevelList.get(k).toString());
            for (int u = 0; u < LevelLists.brewingItemList.get(k).size(); u++) {
                buf.writeString(LevelLists.brewingItemList.get(k).get(u).toString());
            }
        }
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(LIST_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

}
