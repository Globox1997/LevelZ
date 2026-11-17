package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.levelz.editor.RestrictionEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.ArrayList;
import java.util.List;

public record EditorDataPacket(List<RestrictionEntry> entries, List<String> skills, List<String> problems, boolean openScreen) implements CustomPacketPayload {

    public static final int MAX_JSON_LENGTH = 262144;

    public static final CustomPacketPayload.Type<EditorDataPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("editor_data_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditorDataPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.entries.size());
        for (RestrictionEntry entry : value.entries) {
            buf.writeUtf(entry.file());
            buf.writeUtf(entry.key());
            buf.writeUtf(entry.json(), MAX_JSON_LENGTH);
        }
        writeStrings(buf, value.skills);
        writeStrings(buf, value.problems);
        buf.writeBoolean(value.openScreen);
    }, buf -> {
        List<RestrictionEntry> entries = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            entries.add(new RestrictionEntry(buf.readUtf(), buf.readUtf(), buf.readUtf(MAX_JSON_LENGTH)));
        }
        return new EditorDataPacket(entries, readStrings(buf), readStrings(buf), buf.readBoolean());
    });

    public static void writeStrings(RegistryFriendlyByteBuf buf, List<String> strings) {
        buf.writeInt(strings.size());
        for (String string : strings) {
            buf.writeUtf(string);
        }
    }

    public static List<String> readStrings(RegistryFriendlyByteBuf buf) {
        List<String> strings = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            strings.add(buf.readUtf());
        }
        return strings;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
