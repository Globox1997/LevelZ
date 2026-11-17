package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record EnchantmentZPacket(Map<String, Integer> indexed, List<Integer> keys, List<String> ids, List<Integer> levels) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EnchantmentZPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("enchantmentz_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentZPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT).encode(buf, new HashMap<>(value.indexed));
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buf, value.keys);
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(buf, value.ids);
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buf, value.levels);
    }, buf -> new EnchantmentZPacket(ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT).decode(buf), ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buf),
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).decode(buf), ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buf)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

