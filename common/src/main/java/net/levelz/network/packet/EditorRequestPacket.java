package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EditorRequestPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EditorRequestPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("editor_request_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditorRequestPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
    }, buf -> new EditorRequestPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
