package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record PlayerSkillSyncPacket(List<Integer> playerSkillIds, List<Integer> playerSkillLevels) implements CustomPayload {

    public static final CustomPayload.Id<PlayerSkillSyncPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("player_skill_sync_packet"));

    public static final PacketCodec<RegistryByteBuf, PlayerSkillSyncPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeCollection(value.playerSkillIds, PacketByteBuf::writeInt);
        buf.writeCollection(value.playerSkillLevels, PacketByteBuf::writeInt);
    }, buf -> new PlayerSkillSyncPacket(buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readInt)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

