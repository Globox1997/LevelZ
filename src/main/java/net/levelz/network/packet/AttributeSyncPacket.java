package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AttributeSyncPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AttributeSyncPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("attribute_sync_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeSyncPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
    }, buf -> new AttributeSyncPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

