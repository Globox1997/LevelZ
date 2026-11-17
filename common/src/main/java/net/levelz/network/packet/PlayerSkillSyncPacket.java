package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;

public record PlayerSkillSyncPacket(List<Integer> playerSkillIds, List<Integer> playerSkillLevels) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerSkillSyncPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("player_skill_sync_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSkillSyncPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buf, value.playerSkillIds);
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buf, value.playerSkillLevels);
    }, buf -> new PlayerSkillSyncPacket(ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buf), ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buf)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

