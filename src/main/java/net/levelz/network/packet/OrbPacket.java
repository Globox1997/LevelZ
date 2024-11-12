package net.levelz.network.packet;

import net.levelz.access.OrbAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.network.LevelServerPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.Vec3d;

public class OrbPacket implements Packet<ClientPlayPacketListener> {
    public static final PacketCodec<PacketByteBuf, OrbPacket> CODEC = Packet.createCodec(OrbPacket::write, OrbPacket::new);
    private final int entityId;
    private final double x;
    private final double y;
    private final double z;
    private final int experience;

    public OrbPacket(LevelExperienceOrbEntity orb, EntityTrackerEntry entry) {
        this.entityId = orb.getId();
        Vec3d vec3d = entry.getPos();
        this.x = vec3d.getX();
        this.y = vec3d.getY();
        this.z = vec3d.getZ();
        this.experience = orb.getExperienceAmount();
    }

    private OrbPacket(PacketByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.experience = buf.readShort();
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeShort(this.experience);
    }

    @Override
    public PacketType<OrbPacket> getPacketId() {
        return LevelServerPacket.ADD_LEVEL_EXPERIENCE_ORB;
    }

    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        ((OrbAccess) clientPlayPacketListener).onLevelExperienceOrbSpawn(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public int getExperience() {
        return this.experience;
    }
}
//
//
////package net.levelz.network.packet;
////
////import net.levelz.LevelzMain;
////import net.minecraft.network.RegistryByteBuf;
////import net.minecraft.network.codec.PacketCodec;
////import net.minecraft.network.packet.CustomPayload;
////
////public record OrbPacket(int id, double x, double y, double z, int amount) implements CustomPayload {
////
////    public static final CustomPayload.Id<OrbPacket> PACKET_ID = new CustomPayload.Id<>(LevelzMain.identifierOf("orb_packet"));
////
////    public static final PacketCodec<RegistryByteBuf, OrbPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
////        buf.writeInt(value.id);
////        buf.writeDouble(value.x);
////        buf.writeDouble(value.y);
////        buf.writeDouble(value.z);
////        buf.writeInt(value.amount);
////    }, buf -> new OrbPacket(buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt()));
////
////    @Override
////    public Id<? extends CustomPayload> getId() {
////        return PACKET_ID;
////    }
////
////}
////
