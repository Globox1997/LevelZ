package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EditorSavePacket(String key, String json, int baseHash) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EditorSavePacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("editor_save_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditorSavePacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeUtf(value.key);
        buf.writeUtf(value.json, EditorDataPacket.MAX_JSON_LENGTH);
        buf.writeInt(value.baseHash);
    }, buf -> new EditorSavePacket(buf.readUtf(), buf.readUtf(EditorDataPacket.MAX_JSON_LENGTH), buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
