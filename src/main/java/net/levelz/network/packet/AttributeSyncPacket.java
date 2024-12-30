package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record AttributeSyncPacket() implements CustomPayload {

    public static final CustomPayload.Id<AttributeSyncPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("attribute_sync_packet"));

    public static final PacketCodec<RegistryByteBuf, AttributeSyncPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
    }, buf -> new AttributeSyncPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

