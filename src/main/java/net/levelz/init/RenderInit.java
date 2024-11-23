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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final Identifier SKILL_TAB_ICON = LevelzMain.identifierOf("textures/gui/sprites/skill_tab_icon.png");
    public static final Identifier BAG_TAB_ICON = LevelzMain.identifierOf("textures/gui/sprites/bag_tab_icon.png");

    public static final Identifier MINEABLE_INFO = LevelzMain.identifierOf("mineable_info");
    public static final Identifier MINEABLE_LEVEL_INFO = LevelzMain.identifierOf("mineable_level_info");

    public static void init() {
        EntityRendererRegistry.register(EntityInit.LEVEL_EXPERIENCE_ORB, LevelExperienceOrbEntityRenderer::new);

        TabRegistry.registerInventoryTab(new VanillaInventoryTab(Text.translatable("container.crafting"), BAG_TAB_ICON, 0, InventoryScreen.class));
        TabRegistry.registerInventoryTab(new LevelzTab(Text.translatable("screen.levelz.skill_screen"), SKILL_TAB_ICON, 1, LevelScreen.class, SkillInfoScreen.class, SkillRestrictionScreen.class));

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            TooltipUtil.renderTooltip(MinecraftClient.getInstance(), drawContext);
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            TooltipUtil.renderItemTooltip(MinecraftClient.getInstance(), stack, lines);
        });
    }
}
