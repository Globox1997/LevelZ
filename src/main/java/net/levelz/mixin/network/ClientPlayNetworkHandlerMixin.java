package net.levelz.mixin.network;

import net.levelz.access.OrbAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.network.packet.OrbPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler implements OrbAccess {

    @Shadow
    @Mutable
    private ClientWorld world;

    public ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Override
    public void onLevelExperienceOrbSpawn(OrbPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler) (Object) this, this.client);
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        Entity entity = new LevelExperienceOrbEntity(this.world, d, e, f, packet.getExperience());
        entity.updateTrackedPosition(d, e, f);
        entity.setYaw(0.0F);
        entity.setPitch(0.0F);
        entity.setId(packet.getEntityId());
        this.world.addEntity(entity);
    }
}
