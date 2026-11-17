package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EditorExportPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EditorExportPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("editor_export_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditorExportPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
    }, buf -> new EditorExportPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
