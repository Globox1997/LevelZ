package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record EditorExportPacket() implements CustomPayload {

    public static final CustomPayload.Id<EditorExportPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("editor_export_packet"));

    public static final PacketCodec<RegistryByteBuf, EditorExportPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
    }, buf -> new EditorExportPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
