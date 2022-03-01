package net.levelz.init;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.levelz.entity.render.LevelExperienceOrbEntityRenderer;
import net.minecraft.util.Identifier;

public class RenderInit {

    public static final Identifier GUI_ICONS = new Identifier("levelz:textures/gui/icons.png");

    public static void init() {
        EntityRendererRegistry.register(EntityInit.LEVEL_EXPERIENCE_ORB, LevelExperienceOrbEntityRenderer::new);
    }
}
