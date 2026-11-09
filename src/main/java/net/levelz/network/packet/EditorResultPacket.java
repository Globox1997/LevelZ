package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record EditorResultPacket(String key, boolean success, String message, List<String> arguments, List<String> problems) implements CustomPayload {

    public static final CustomPayload.Id<EditorResultPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("editor_result_packet"));

    public static final PacketCodec<RegistryByteBuf, EditorResultPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeString(value.key);
        buf.writeBoolean(value.success);
        buf.writeString(value.message);
        EditorDataPacket.writeStrings(buf, value.arguments);
        EditorDataPacket.writeStrings(buf, value.problems);
    }, buf -> new EditorResultPacket(buf.readString(), buf.readBoolean(), buf.readString(), EditorDataPacket.readStrings(buf), EditorDataPacket.readStrings(buf)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
