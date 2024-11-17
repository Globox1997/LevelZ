package net.levelz.waila;

import mcp.mobius.waila.api.*;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.RenderInit;
import net.levelz.level.LevelManager;
import net.minecraft.block.Block;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

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
            LevelManager levelManager = ((LevelManagerAccess) accessor.getPlayer()).getLevelManager();
            if (!levelManager.hasRequiredMiningLevel(accessor.getBlock())) {
                for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredMiningLevel(accessor.getBlock()).entrySet()) {
                    Formatting formatting =
                            levelManager.getSkillLevel(entry.getKey())< entry.getValue() ? Formatting.RED: Formatting.GREEN;
                    tooltip.addLine(Text.translatable("item.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(formatting));
                }
            }
        }
    }
}
