package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;
import java.util.Map;

public record EnchantmentZPacket(Map<String, Integer> indexed, List<Integer> keys, List<String> ids, List<Integer> levels) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EnchantmentZPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("enchantmentz_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentZPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeMap(value.indexed, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
        buf.writeCollection(value.keys, FriendlyByteBuf::writeInt);
        buf.writeCollection(value.ids, FriendlyByteBuf::writeUtf);
        buf.writeCollection(value.levels, FriendlyByteBuf::writeInt);
    }, buf -> new EnchantmentZPacket(buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt), buf.readList(FriendlyByteBuf::readInt), buf.readList(FriendlyByteBuf::readUtf), buf.readList(FriendlyByteBuf::readInt)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

