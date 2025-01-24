package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;
import java.util.Map;

public record EnchantmentZPacket(Map<String, Integer> indexed, List<Integer> keys, List<String> ids, List<Integer> levels) implements CustomPayload {

    public static final CustomPayload.Id<EnchantmentZPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("enchantmentz_packet"));

    public static final PacketCodec<RegistryByteBuf, EnchantmentZPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeMap(value.indexed, PacketByteBuf::writeString, PacketByteBuf::writeInt);
        buf.writeCollection(value.keys, PacketByteBuf::writeInt);
        buf.writeCollection(value.ids, PacketByteBuf::writeString);
        buf.writeCollection(value.levels, PacketByteBuf::writeInt);
    }, buf -> new EnchantmentZPacket(buf.readMap(PacketByteBuf::readString, PacketByteBuf::readInt), buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readInt)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

