package net.levelz.waila;

import net.levelz.access.LevelManagerAccess;
import net.levelz.init.RenderInit;
import net.levelz.level.LevelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Map;

public enum LevelEntityJadeProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return RenderInit.MINEABLE_INFO;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        LevelManager levelManager = ((LevelManagerAccess) entityAccessor.getPlayer()).getLevelManager();
        if (!levelManager.hasRequiredEntityLevel(entityAccessor.getEntity().getType())) {
            for (Map.Entry<Integer, Integer> entry : levelManager.getRequiredEntityLevel(entityAccessor.getEntity().getType()).entrySet()) {
                ChatFormatting formatting =
                        levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? ChatFormatting.RED : ChatFormatting.GREEN;
                tooltip.add(Component.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).withStyle(formatting));
            }
        }
    }
}
