package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;

public record EditorResultPacket(String key, boolean success, String message, List<String> arguments, List<String> problems) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EditorResultPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("editor_result_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditorResultPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeUtf(value.key);
        buf.writeBoolean(value.success);
        buf.writeUtf(value.message);
        EditorDataPacket.writeStrings(buf, value.arguments);
        EditorDataPacket.writeStrings(buf, value.problems);
    }, buf -> new EditorResultPacket(buf.readUtf(), buf.readBoolean(), buf.readUtf(), EditorDataPacket.readStrings(buf), EditorDataPacket.readStrings(buf)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
