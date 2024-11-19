package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.levelz.level.PlayerRestriction;
import net.levelz.level.restriction.EnchantmentRestriction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.*;

public record RestrictionPacket(RestrictionRecord blockRestrictions, RestrictionRecord craftingRestrictions, RestrictionRecord entityRestrictions,
                                RestrictionRecord itemRestrictions, RestrictionRecord miningRestrictions, EnchantmentRestrictionRecord enchantmentRestrictions) implements CustomPayload {

    public static final CustomPayload.Id<RestrictionPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("restriction_packet"));

    public static final PacketCodec<RegistryByteBuf, RestrictionPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        value.blockRestrictions.write(buf);
        value.craftingRestrictions.write(buf);
        value.entityRestrictions.write(buf);
        value.itemRestrictions.write(buf);
        value.miningRestrictions.write(buf);
        value.enchantmentRestrictions.write(buf);
    }, buf -> new RestrictionPacket(RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), EnchantmentRestrictionRecord.read(buf)));

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

    public record EnchantmentRestrictionRecord(List<String> ids, List<EnchantmentRestriction> restrictions) {

        public void write(PacketByteBuf buf) {
            buf.writeInt(ids().size());
            for (String id : ids) {
                buf.writeString(id);
            }
            buf.writeInt(restrictions().size());
            for (int i = 0; i < restrictions().size(); i++) {
                EnchantmentRestriction enchantmentRestriction = restrictions().get(i);
                buf.writeString(enchantmentRestriction.getEnchantment().getIdAsString());
                buf.writeInt(enchantmentRestriction.getSkillLevelRestrictions().size());
                for (Map.Entry<Integer, Map<Integer, Integer>> entry : enchantmentRestriction.getSkillLevelRestrictions().entrySet()) {
                    buf.writeInt(entry.getKey());
                    buf.writeInt(entry.getValue().size());
                    for (Map.Entry<Integer, Integer> enchantmentEntry : entry.getValue().entrySet()) {
                        buf.writeInt(enchantmentEntry.getKey());
                        buf.writeInt(enchantmentEntry.getValue());
                    }
                }
            }
        }

        public static EnchantmentRestrictionRecord read(PacketByteBuf buf) {
            Optional<RegistryWrapper.Impl<Enchantment>> wrapper = BuiltinRegistries.createWrapperLookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT);
            List<String> ids = new ArrayList<>();
            List<EnchantmentRestriction> enchantmentRestrictions = new ArrayList<>();
            if (wrapper.isPresent()) {
                int idSize = buf.readInt();
                for (int i = 0; i < idSize; i++) {
                    ids.add(buf.readString());
                }
                int size = buf.readInt();
                for (int i = 0; i < size; i++) {
                    String id = buf.readString();

                    int enchantmentSize = buf.readInt();
                    Map<Integer, Map<Integer, Integer>> skillLevelRestrictions = new HashMap<>();

                    for (int u = 0; u < enchantmentSize; u++) {
                        int enchantmentLevel = buf.readInt();
                        int restrictions = buf.readInt();
                        Map<Integer, Integer> enchantmentRestriction = new HashMap<>();
                        for (int o = 0; o < restrictions; o++) {
                            int skillId = buf.readInt();
                            int skillLevel = buf.readInt();
                            enchantmentRestriction.put(skillId, skillLevel);
                        }
                        skillLevelRestrictions.put(enchantmentLevel,enchantmentRestriction);
                    }
                    enchantmentRestrictions.add(new EnchantmentRestriction(wrapper.get().getOptional(RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(id))).get(), skillLevelRestrictions));
                }
            }
            return new EnchantmentRestrictionRecord(ids, enchantmentRestrictions);
        }

    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

