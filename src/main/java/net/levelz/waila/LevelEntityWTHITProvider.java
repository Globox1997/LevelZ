package net.levelz.waila;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.RenderInit;
import net.levelz.level.LevelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.Map;

public class LevelEntityWTHITProvider implements IEntityComponentProvider {

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        IEntityComponentProvider.super.appendBody(tooltip, accessor, config);
        if (config.getBoolean(RenderInit.MINEABLE_INFO)) {
            LevelManager levelManager = ((LevelManagerAccess) accessor.getPlayer()).getLevelManager();
            if (!levelManager.hasRequiredEntityLevel(accessor.getEntity().getType())) {
                for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredEntityLevel(accessor.getEntity().getType()).entrySet()) {
                    ChatFormatting formatting =
                            levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? ChatFormatting.RED : ChatFormatting.GREEN;
                    tooltip.addLine(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(formatting));
                }
            }
        }
    }

}
