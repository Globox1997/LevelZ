package net.levelz.access;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerStatsManagerAccess {

    public PlayerStatsManager getPlayerStatsManager(PlayerEntity player);
}