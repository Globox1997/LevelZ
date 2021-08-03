package net.levelz.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.MinecraftClient;
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
        playerStatsManager.setLevel("defence", buf.readInt());
        playerStatsManager.setLevel("stamina", buf.readInt());
        playerStatsManager.setLevel("luck", buf.readInt());
        playerStatsManager.setLevel("archery", buf.readInt());
        playerStatsManager.setLevel("trade", buf.readInt());
        playerStatsManager.setLevel("smithing", buf.readInt());
        playerStatsManager.setLevel("mining", buf.readInt());
        playerStatsManager.setLevel("farming", buf.readInt());
        playerStatsManager.setLevel("alchemy", buf.readInt());
        // Set unlocked block list
        // playerStatsManager.lockedBlockIds.clear();
        PlayerStatsServerPacket.syncLockedBlockList(playerStatsManager);
        PlayerStatsServerPacket.syncLockedBrewingItemList(playerStatsManager);
    }

}