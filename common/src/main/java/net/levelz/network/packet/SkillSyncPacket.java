package net.levelz.network.packet;

import net.levelz.LevelzMain;
import net.levelz.level.SkillAttribute;
import net.levelz.level.SkillBonus;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.ArrayList;
import java.util.List;

public record SkillSyncPacket(List<Integer> skillIds, List<String> skillKeys, List<Integer> skillMaxLevels, List<SkillAttributesRecord> skillAttributes,
                              SkillBonusesRecord skillBonuses) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SkillSyncPacket> PACKET_ID = new CustomPacketPayload.Type<>(LevelzMain.identifierOf("skill_sync_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillSyncPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buf, value.skillIds);
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(buf, value.skillKeys);
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buf, value.skillMaxLevels);
        buf.writeVarInt(value.skillAttributes.size());
        for (SkillAttributesRecord skillAttributesRecord : value.skillAttributes) {
            skillAttributesRecord.write(buf);
        }
        value.skillBonuses.write(buf);
    }, buf -> {
        List<Integer> skillIds = ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buf);
        List<String> skillKeys = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).decode(buf);
        List<Integer> skillMaxLevels = ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buf);
        List<SkillAttributesRecord> skillAttributes = new ArrayList<>();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            skillAttributes.add(SkillAttributesRecord.read(buf));
        }
        return new SkillSyncPacket(skillIds, skillKeys, skillMaxLevels, skillAttributes, SkillBonusesRecord.read(buf));
    });


    public record SkillAttributesRecord(List<SkillAttribute> skillAttributes) {

        public void write(FriendlyByteBuf buf) {
            buf.writeInt(skillAttributes().size());
            for (int i = 0; i < skillAttributes().size(); i++) {
                SkillAttribute skillAttribute = skillAttributes().get(i);
                buf.writeInt(skillAttribute.getId());
                buf.writeUtf(skillAttribute.getAttibute().getRegisteredName());
                buf.writeFloat(skillAttribute.getBaseValue());
                buf.writeFloat(skillAttribute.getLevelValue());
                buf.writeUtf(skillAttribute.getOperation().getSerializedName());

            }
        }

        public static SkillAttributesRecord read(FriendlyByteBuf buf) {
            List<SkillAttribute> skillAttributes = new ArrayList<>();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                int id = buf.readInt();
                Holder<Attribute> attibute = BuiltInRegistries.ATTRIBUTE.get(Identifier.parse(buf.readUtf())).get();
                float baseValue = buf.readFloat();
                float levelValue = buf.readFloat();
                AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(buf.readUtf().toUpperCase());
                skillAttributes.add(new SkillAttribute(id, attibute, baseValue, levelValue, operation));
            }
            return new SkillAttributesRecord(skillAttributes);
        }

    }

    public record SkillBonusesRecord(List<SkillBonus> skillBonuses) {

        public void write(FriendlyByteBuf buf) {
            buf.writeInt(skillBonuses().size());
            for (int i = 0; i < skillBonuses().size(); i++) {
                SkillBonus skillBonus = skillBonuses().get(i);
                buf.writeUtf(skillBonus.getKey());
                buf.writeInt(skillBonus.getId());
                buf.writeInt(skillBonus.getLevel());
            }
        }

        public static SkillBonusesRecord read(FriendlyByteBuf buf) {
            List<SkillBonus> skillBonuses = new ArrayList<>();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                String key = buf.readUtf();
                int id = buf.readInt();
                int level = buf.readInt();
                skillBonuses.add(new SkillBonus(key, id, level));
            }
            return new SkillBonusesRecord(skillBonuses);
        }

    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

//package net.levelz.network.packet;
//
//import net.levelz.LevelzMain;
//import net.levelz.level.SkillAttribute;
//import net.levelz.level.SkillBonus;
//import net.minecraft.entity.attribute.EntityAttribute;
//import net.minecraft.entity.attribute.EntityAttributeModifier;
//import net.minecraft.network.PacketByteBuf;
//import net.minecraft.network.RegistryByteBuf;
//import net.minecraft.network.codec.PacketCodec;
//import net.minecraft.network.packet.CustomPayload;
//import net.minecraft.registry.Registries;
//import net.minecraft.registry.entry.RegistryEntry;
//import net.minecraft.util.Identifier;
//
//import java.util.ArrayList;
//import java.util.List;
//
////public record SkillSyncPacket(List<Integer> skillIds, List<String> skillKeys, List<Integer> skillMaxLevels, List<SkillAttributesRecord> skillAttributes,
////                              List<SkillBonusesRecord> skillBonuses) implements CustomPayload {
//
//public record SkillSyncPacket(List<Integer> skillIds, List<String> skillKeys, List<Integer> skillMaxLevels, SkillAttributesRecord skillAttributes,
//                              SkillBonusesRecord skillBonuses) implements CustomPayload {
//
//    public static final CustomPayload.Id<SkillSyncPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("skill_sync_packet"));
//
//    public static final PacketCodec<RegistryByteBuf, SkillSyncPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
//        buf.writeCollection(value.skillIds, PacketByteBuf::writeInt);
//        buf.writeCollection(value.skillKeys, PacketByteBuf::writeString);
//        buf.writeCollection(value.skillMaxLevels, PacketByteBuf::writeInt);
//
//        value.skillAttributes.write(buf);
//        value.skillBonuses.write(buf);
////        buf.writeCollection(value.skillAttributes, (bufx, list) -> new SkillAttributesRecord(list.skillAttributes()).write(bufx));
////        buf.writeCollection(value.skillBonuses, (bufx, list) -> new SkillBonusesRecord(list.skillBonuses()).write(bufx));
//
////    }, buf -> new SkillSyncPacket(buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readInt), buf.readList(SkillAttributesRecord::read), buf.readList(SkillBonusesRecord::read)));
//    }, buf -> new SkillSyncPacket(buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readInt), SkillAttributesRecord.read(buf), SkillBonusesRecord.read(buf)));
//
//
//    public record SkillAttributesRecord(List<SkillAttribute> skillAttributes) {
//
//        public void write(PacketByteBuf buf) {
//            buf.writeInt(skillAttributes().size());
//            for (int i = 0; i < skillAttributes().size(); i++) {
//                SkillAttribute skillAttribute = skillAttributes().get(i);
//                buf.writeString(skillAttribute.getAttibute().getIdAsString());
//                buf.writeFloat(skillAttribute.getBaseValue());
//                buf.writeFloat(skillAttribute.getLevelValue());
//                buf.writeString(skillAttribute.getOperation().asString());
//
//            }
//        }
//
//        public static SkillAttributesRecord read(PacketByteBuf buf) {
//            List<SkillAttribute> skillAttributes = new ArrayList<>();
//            int size = buf.readInt();
//            for (int i = 0; i < size; i++) {
//                RegistryEntry<EntityAttribute> attibute = Registries.ATTRIBUTE.getEntry(Identifier.of(buf.readString())).get();
//                float baseValue = buf.readFloat();
//                float levelValue = buf.readFloat();
//                EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.valueOf(buf.readString().toUpperCase());
//                skillAttributes.add(new SkillAttribute(attibute, baseValue, levelValue, operation));
//            }
//            return new SkillAttributesRecord(skillAttributes);
//        }
//
//    }
//
//    public record SkillBonusesRecord(List<SkillBonus> skillBonuses) {
//
//        public void write(PacketByteBuf buf) {
//            buf.writeInt(skillBonuses().size());
//            for (int i = 0; i < skillBonuses().size(); i++) {
//                SkillBonus skillBonus = skillBonuses().get(i);
//                buf.writeInt(skillBonus.getId());
//                buf.writeInt(skillBonus.getLevel());
//                buf.writeString(skillBonus.getKey());
//            }
//        }
//
//        public static SkillBonusesRecord read(PacketByteBuf buf) {
//            List<SkillBonus> skillBonuses = new ArrayList<>();
//            int size = buf.readInt();
//            for (int i = 0; i < size; i++) {
//                int id = buf.readInt();
//                int level = buf.readInt();
//                String key = buf.readString();
//                skillBonuses.add(new SkillBonus(id, level, key));
//            }
//            return new SkillBonusesRecord(skillBonuses);
//        }
//
//    }
//
//
//    @Override
//    public Id<? extends CustomPayload> getId() {
//        return PACKET_ID;
//    }
//
//}


