package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.levelz.level.restriction.ComponentRestriction;
import net.levelz.level.restriction.PlayerRestriction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record RestrictionPacket(RestrictionRecord blockRestrictions, RestrictionRecord craftingRestrictions, RestrictionRecord entityRestrictions,
                                RestrictionRecord itemRestrictions, RestrictionRecord miningRestrictions, RestrictionRecord enchantmentRestrictions,
                                List<ComponentRestriction> componentRestrictions) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RestrictionPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("restriction_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RestrictionPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        value.blockRestrictions.write(buf);
        value.craftingRestrictions.write(buf);
        value.entityRestrictions.write(buf);
        value.itemRestrictions.write(buf);
        value.miningRestrictions.write(buf);
        value.enchantmentRestrictions.write(buf);
        writeComponentRestrictions(buf, value.componentRestrictions);
    }, buf -> new RestrictionPacket(RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf),
            readComponentRestrictions(buf)));

    private static void writeComponentRestrictions(RegistryFriendlyByteBuf buf, List<ComponentRestriction> componentRestrictions) {
        buf.writeInt(componentRestrictions.size());
        for (ComponentRestriction componentRestriction : componentRestrictions) {
            buf.writeInt(componentRestriction.getId());
            DataComponentPatch.STREAM_CODEC.encode(buf, componentRestriction.getComponents());
            buf.writeInt(componentRestriction.getSkillLevelRestrictions().size());
            for (Map.Entry<Integer, Integer> entry : componentRestriction.getSkillLevelRestrictions().entrySet()) {
                buf.writeInt(entry.getKey());
                buf.writeInt(entry.getValue());
            }
        }
    }

    private static List<ComponentRestriction> readComponentRestrictions(RegistryFriendlyByteBuf buf) {
        List<ComponentRestriction> componentRestrictions = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            int id = buf.readInt();
            DataComponentPatch components = DataComponentPatch.STREAM_CODEC.decode(buf);
            int skillLevelSize = buf.readInt();
            Map<Integer, Integer> skillLevelRestrictions = new HashMap<>();
            for (int u = 0; u < skillLevelSize; u++) {
                skillLevelRestrictions.put(buf.readInt(), buf.readInt());
            }
            componentRestrictions.add(new ComponentRestriction(id, components, skillLevelRestrictions));
        }
        return componentRestrictions;
    }

    public record RestrictionRecord(List<Integer> ids, List<PlayerRestriction> restrictions) {

        public void write(FriendlyByteBuf buf) {
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

        public static RestrictionRecord read(FriendlyByteBuf buf) {
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
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

