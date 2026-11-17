package net.levelz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.levelz.LevelzMain;
import net.levelz.entity.render.LevelExperienceOrbEntityRenderer;
import net.levelz.screen.SkillInfoScreen;
import net.levelz.screen.SkillRestrictionScreen;
import net.levelz.screen.LevelScreen;
import net.levelz.screen.widget.LevelzTab;
import net.levelz.screen.widget.VanillaInventoryTab;
import net.levelz.util.TooltipUtil;
import net.libz.registry.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final ResourceLocation SKILL_TAB_ICON = LevelzMain.identifierOf("textures/gui/sprites/skill_tab_icon.png");
    public static final ResourceLocation BAG_TAB_ICON = LevelzMain.identifierOf("textures/gui/sprites/bag_tab_icon.png");

    public static final ResourceLocation MINEABLE_INFO = LevelzMain.identifierOf("mineable_info");
    public static final ResourceLocation MINEABLE_LEVEL_INFO = LevelzMain.identifierOf("mineable_level_info");

    public static void init() {
        EntityRendererRegistry.register(EntityInit.LEVEL_EXPERIENCE_ORB, LevelExperienceOrbEntityRenderer::new);

        TabRegistry.registerInventoryTab(new VanillaInventoryTab(Component.translatable("container.crafting"), BAG_TAB_ICON, 0, InventoryScreen.class));
        TabRegistry.registerInventoryTab(new LevelzTab(Component.translatable("screen.levelz.skill_screen"), SKILL_TAB_ICON, 1, LevelScreen.class, SkillInfoScreen.class, SkillRestrictionScreen.class));

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            TooltipUtil.renderTooltip(Minecraft.getInstance(), drawContext);
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            TooltipUtil.renderItemTooltip(Minecraft.getInstance(), stack, lines);
        });
    }
}
