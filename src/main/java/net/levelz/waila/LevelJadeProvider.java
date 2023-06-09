package net.levelz.waila;

import net.levelz.init.RenderInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum LevelJadeProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public Identifier getUid() {
        return RenderInit.MINEABLE_INFO;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (PlayerStatsManager.listContainsItemOrBlock(accessor.getPlayer(), Registries.BLOCK.getRawId(accessor.getBlock()), 1)) {
            tooltip.add(Text.translatable("block.levelz.locked_with_level.tooltip", PlayerStatsManager.getUnlockLevel(Registries.BLOCK.getRawId(accessor.getBlock()), 1)).formatted(Formatting.RED));
        }
    }

}
