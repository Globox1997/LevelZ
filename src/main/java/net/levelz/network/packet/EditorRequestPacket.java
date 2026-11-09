package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record EditorRequestPacket() implements CustomPayload {

    public static final CustomPayload.Id<EditorRequestPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("editor_request_packet"));

    public static final PacketCodec<RegistryByteBuf, EditorRequestPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
    }, buf -> new EditorRequestPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
