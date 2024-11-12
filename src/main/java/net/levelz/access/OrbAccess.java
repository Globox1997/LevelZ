package net.levelz.access;

import net.levelz.network.packet.OrbPacket;

public interface OrbAccess {

    void onLevelExperienceOrbSpawn(OrbPacket packet);
}
