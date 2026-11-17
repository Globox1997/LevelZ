package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Increase skill packet
 * Used in the skill screen by using a button
 *
 * @param id    skill id
 * @param level amount
 */
public record StatPacket(int id, int level) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StatPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("stat_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StatPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.id);
        buf.writeInt(value.level);
    }, buf -> new StatPacket(buf.readInt(), buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

