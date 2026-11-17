package net.levelz.waila;

import net.levelz.access.LevelManagerAccess;
import net.levelz.init.RenderInit;
import net.levelz.level.LevelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Map;

public enum LevelJadeProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public Identifier getUid() {
        return RenderInit.MINEABLE_INFO;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        LevelManager levelManager = ((LevelManagerAccess) accessor.getPlayer()).getLevelManager();
        if (!levelManager.hasRequiredMiningLevel(accessor.getBlock())) {
            for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredMiningLevel(accessor.getBlock()).entrySet()) {
                ChatFormatting formatting =
                        levelManager.getSkillLevel(entry.getKey())< entry.getValue() ? ChatFormatting.RED: ChatFormatting.GREEN;
                tooltip.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(formatting));
            }
        }
    }

}
