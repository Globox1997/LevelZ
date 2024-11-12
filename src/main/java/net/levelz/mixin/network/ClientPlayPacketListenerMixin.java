package net.levelz.mixin.network;

import net.levelz.access.OrbAccess;
import net.levelz.network.packet.OrbPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayPacketListener.class)
public interface ClientPlayPacketListenerMixin extends OrbAccess {

    @Override
    void onLevelExperienceOrbSpawn(OrbPacket packet);
}
