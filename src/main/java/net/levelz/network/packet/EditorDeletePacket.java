package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record EditorDeletePacket(String key, int baseHash) implements CustomPayload {

    public static final CustomPayload.Id<EditorDeletePacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("editor_delete_packet"));

    public static final PacketCodec<RegistryByteBuf, EditorDeletePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeString(value.key);
        buf.writeInt(value.baseHash);
    }, buf -> new EditorDeletePacket(buf.readString(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
