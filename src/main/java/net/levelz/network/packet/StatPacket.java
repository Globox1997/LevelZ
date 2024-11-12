package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Increase skill packet
 * Used in the skill screen by using a button
 *
 * @param id    skill id
 * @param level amount
 */
public record StatPacket(int id, int level) implements CustomPayload {

    public static final CustomPayload.Id<StatPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("stat_packet"));

    public static final PacketCodec<RegistryByteBuf, StatPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.id);
        buf.writeInt(value.level);
    }, buf -> new StatPacket(buf.readInt(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

