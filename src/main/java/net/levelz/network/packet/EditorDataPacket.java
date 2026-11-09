package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.levelz.editor.RestrictionEntry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.List;

public record EditorDataPacket(List<RestrictionEntry> entries, List<String> skills, List<String> problems, boolean openScreen) implements CustomPayload {

    public static final int MAX_JSON_LENGTH = 262144;

    public static final CustomPayload.Id<EditorDataPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("editor_data_packet"));

    public static final PacketCodec<RegistryByteBuf, EditorDataPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.entries.size());
        for (RestrictionEntry entry : value.entries) {
            buf.writeString(entry.file());
            buf.writeString(entry.key());
            buf.writeString(entry.json(), MAX_JSON_LENGTH);
        }
        writeStrings(buf, value.skills);
        writeStrings(buf, value.problems);
        buf.writeBoolean(value.openScreen);
    }, buf -> {
        List<RestrictionEntry> entries = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            entries.add(new RestrictionEntry(buf.readString(), buf.readString(), buf.readString(MAX_JSON_LENGTH)));
        }
        return new EditorDataPacket(entries, readStrings(buf), readStrings(buf), buf.readBoolean());
    });

    public static void writeStrings(RegistryByteBuf buf, List<String> strings) {
        buf.writeInt(strings.size());
        for (String string : strings) {
            buf.writeString(string);
        }
    }

    public static List<String> readStrings(RegistryByteBuf buf) {
        List<String> strings = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            strings.add(buf.readString());
        }
        return strings;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
