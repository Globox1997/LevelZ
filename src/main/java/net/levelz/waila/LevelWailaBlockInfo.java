package net.levelz.waila;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import net.levelz.init.RenderInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LevelWailaBlockInfo extends LevelFeature implements IBlockComponentProvider {

    @Override
    public void initialize(IRegistrar registrar) {
        registrar.addConfig(RenderInit.MINEABLE_INFO, true);
        registrar.addConfig(RenderInit.MINEABLE_LEVEL_INFO, false);
        registrar.addComponent(this, TooltipPosition.BODY, Block.class);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        IBlockComponentProvider.super.appendBody(tooltip, accessor, config);
        if (config.getBoolean(RenderInit.MINEABLE_INFO)) {
            if (PlayerStatsManager.listContainsItemOrBlock(accessor.getPlayer(), Registries.BLOCK.getRawId(accessor.getBlock()), 1)) {
                if (config.getBoolean(RenderInit.MINEABLE_LEVEL_INFO))
                    tooltip.addLine(Text.translatable("block.levelz.locked_with_level.tooltip", PlayerStatsManager.getUnlockLevel(Registries.BLOCK.getRawId(accessor.getBlock()), 1))
                            .formatted(Formatting.RED));
                else
                    tooltip.addLine(Text.translatable("block.levelz.locked.tooltip"));
            }
        }
    }
}
