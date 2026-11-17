package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EditorDeletePacket(String key, int baseHash) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EditorDeletePacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("editor_delete_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditorDeletePacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeUtf(value.key);
        buf.writeInt(value.baseHash);
    }, buf -> new EditorDeletePacket(buf.readUtf(), buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
