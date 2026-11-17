package net.levelz.network.packet;

import net.levelz.access.OrbAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.network.LevelServerPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.phys.Vec3;

public class OrbPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, OrbPacket> CODEC = Packet.codec(OrbPacket::write, OrbPacket::new);
    private final int entityId;
    private final double x;
    private final double y;
    private final double z;
    private final int experience;

    public OrbPacket(LevelExperienceOrbEntity orb, ServerEntity entry) {
        this.entityId = orb.getId();
        Vec3 vec3d = entry.getPositionBase();
        this.x = vec3d.x();
        this.y = vec3d.y();
        this.z = vec3d.z();
        this.experience = orb.getExperienceAmount();
    }

    private OrbPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.experience = buf.readShort();
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeShort(this.experience);
    }

    @Override
    public PacketType<OrbPacket> type() {
        return LevelServerPacket.ADD_LEVEL_EXPERIENCE_ORB;
    }

    public void handle(ClientGamePacketListener clientPlayPacketListener) {
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
