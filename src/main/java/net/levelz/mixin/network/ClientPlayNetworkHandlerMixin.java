package net.levelz.mixin.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.OrbAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.network.packet.OrbPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonPacketListenerImpl implements OrbAccess {

    @Shadow
    @Mutable
    private ClientLevel level;

    public ClientPlayNetworkHandlerMixin(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
        super(client, connection, connectionState);
    }

    @Override
    public void onLevelExperienceOrbSpawn(OrbPacket packet) {
        PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener) (Object) this, this.minecraft);
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        Entity entity = new LevelExperienceOrbEntity(this.level, d, e, f, packet.getExperience());
        entity.syncPacketPositionCodec(d, e, f);
        entity.setYRot(0.0F);
        entity.setXRot(0.0F);
        entity.setId(packet.getEntityId());
        this.level.addEntity(entity);
    }
}
