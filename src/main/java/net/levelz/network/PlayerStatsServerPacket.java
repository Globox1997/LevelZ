package net.levelz.network;

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
                } else if (stat.equals("defence")) {
                    player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR) + 0.2D);
                } else if (stat.equals("luck")) {
                    player.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR) + 0.05D);
                } else if (stat.equals("mining")) {
                    syncLockedBlockList(playerStatsManager);
                    // for (int i = 0; i < LevelJsonInit.MINING_LEVEL_LIST.size(); i++) {
                    // if (LevelJsonInit.MINING_LEVEL_LIST.get(i) < playerStatsManager.getLevel("mining")) {
                    // for (int u = 0; u < LevelJsonInit.MINING_BLOCK_LIST.get(i).size(); u++) {
                    // if (!playerStatsManager.unlockedBlocks.contains(LevelJsonInit.MINING_BLOCK_LIST.get(i).get(u))) {
                    // playerStatsManager.unlockedBlocks.add(LevelJsonInit.MINING_BLOCK_LIST.get(i).get(u));
                    // }
                    // }
                    // }
                    // }

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
        buf.writeInt(playerStatsManager.getLevel("defence"));
        buf.writeInt(playerStatsManager.getLevel("stamina"));
        buf.writeInt(playerStatsManager.getLevel("luck"));
        buf.writeInt(playerStatsManager.getLevel("archery"));
        buf.writeInt(playerStatsManager.getLevel("trade"));
        buf.writeInt(playerStatsManager.getLevel("smithing"));
        buf.writeInt(playerStatsManager.getLevel("mining"));
        buf.writeInt(playerStatsManager.getLevel("farming"));
        buf.writeInt(playerStatsManager.getLevel("building"));

        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(LEVEL_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);

        // Set on server
        // System.out.println("WriteSkillPacket");
        syncLockedBlockList(playerStatsManager);
    }

    public static void syncLockedBlockList(PlayerStatsManager playerStatsManager) {
        playerStatsManager.lockedBlockIds.clear();

        for (int i = 0; i < LevelLists.miningLevelList.size(); i++) {
            if (LevelLists.miningLevelList.get(i) > playerStatsManager.getLevel("mining")) {
                for (int u = 0; u < LevelLists.miningBlockList.get(i).size(); u++) {
                    if (!playerStatsManager.lockedBlockIds.contains(LevelLists.miningBlockList.get(i).get(u))) {
                        // System.out.println("Add to List:" + JsonReaderInit.miningBlockList.get(i).get(u));
                        playerStatsManager.lockedBlockIds.add(LevelLists.miningBlockList.get(i).get(u));
                    }
                }
            }
        }
    }

}
