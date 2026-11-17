package net.levelz.mixin.network;

import net.levelz.access.OrbAccess;
import net.levelz.network.packet.OrbPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientGamePacketListener.class)
public interface ClientPlayPacketListenerMixin extends OrbAccess {

    @Override
    void onLevelExperienceOrbSpawn(OrbPacket packet);
}
