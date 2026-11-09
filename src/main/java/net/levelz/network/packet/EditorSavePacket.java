package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record EditorSavePacket(String key, String json, int baseHash) implements CustomPayload {

    public static final CustomPayload.Id<EditorSavePacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("editor_save_packet"));

    public static final PacketCodec<RegistryByteBuf, EditorSavePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeString(value.key);
        buf.writeString(value.json, EditorDataPacket.MAX_JSON_LENGTH);
        buf.writeInt(value.baseHash);
    }, buf -> new EditorSavePacket(buf.readString(), buf.readString(EditorDataPacket.MAX_JSON_LENGTH), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
