package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.levelz.level.restriction.PlayerRestriction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record RestrictionPacket(RestrictionRecord blockRestrictions, RestrictionRecord craftingRestrictions, RestrictionRecord entityRestrictions,
                                RestrictionRecord itemRestrictions, RestrictionRecord miningRestrictions, RestrictionRecord enchantmentRestrictions) implements CustomPayload {

    public static final CustomPayload.Id<RestrictionPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("restriction_packet"));

    public static final PacketCodec<RegistryByteBuf, RestrictionPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        value.blockRestrictions.write(buf);
        value.craftingRestrictions.write(buf);
        value.entityRestrictions.write(buf);
        value.itemRestrictions.write(buf);
        value.miningRestrictions.write(buf);
        value.enchantmentRestrictions.write(buf);
    }, buf -> new RestrictionPacket(RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf)));

    public record RestrictionRecord(List<Integer> ids, List<PlayerRestriction> restrictions) {

        public void write(PacketByteBuf buf) {
            buf.writeInt(ids().size());
            for (Integer id : ids) {
                buf.writeInt(id);
            }
            buf.writeInt(restrictions().size());
            for (int i = 0; i < restrictions().size(); i++) {
                PlayerRestriction playerRestriction = restrictions().get(i);
                buf.writeInt(playerRestriction.getId());
                buf.writeInt(playerRestriction.getSkillLevelRestrictions().size());
                for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                    buf.writeInt(entry.getKey());
                    buf.writeInt(entry.getValue());
                }
            }
        }

        public static RestrictionRecord read(PacketByteBuf buf) {
            List<Integer> ids = new ArrayList<>();
            int idSize = buf.readInt();
            for (int i = 0; i < idSize; i++) {
                ids.add(buf.readInt());
            }
            List<PlayerRestriction> playerRestrictions = new ArrayList<>();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                int id = buf.readInt();
                int skillLevelSize = buf.readInt();
                Map<Integer, Integer> skillLevelRestrictions = new HashMap<>();
                for (int u = 0; u < skillLevelSize; u++) {
                    int skillId = buf.readInt();
                    int skillLevel = buf.readInt();
                    skillLevelRestrictions.put(skillId, skillLevel);
                }
                playerRestrictions.add(new PlayerRestriction(id, skillLevelRestrictions));
            }
            return new RestrictionRecord(ids, playerRestrictions);
        }

    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

