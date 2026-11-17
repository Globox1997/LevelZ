package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record LevelPacket(int overallLevel, int skillPoints, int totalLevelExperience, float levelProgress) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LevelPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("level_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LevelPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.overallLevel);
        buf.writeInt(value.skillPoints);
        buf.writeInt(value.totalLevelExperience);
        buf.writeFloat(value.levelProgress);
    }, buf -> new LevelPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

