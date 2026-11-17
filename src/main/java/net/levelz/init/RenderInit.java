package net.levelz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.levelz.access.LevelManagerAccess;
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
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final Identifier SKILL_TAB_ICON = LevelzMain.identifierOf("textures/gui/sprites/skill_tab_icon.png");
    public static final Identifier BAG_TAB_ICON = LevelzMain.identifierOf("textures/gui/sprites/bag_tab_icon.png");

    public static final Identifier MINEABLE_INFO = LevelzMain.identifierOf("mineable_info");
    public static final Identifier MINEABLE_LEVEL_INFO = LevelzMain.identifierOf("mineable_level_info");

    public static void init() {
        EntityRendererRegistry.register(EntityInit.LEVEL_EXPERIENCE_ORB, LevelExperienceOrbEntityRenderer::new);

        TabRegistry.registerInventoryTab(new VanillaInventoryTab(Component.translatable("container.crafting"), BAG_TAB_ICON, 0, InventoryScreen.class));
        TabRegistry.registerInventoryTab(new LevelzTab(Component.translatable("screen.levelz.skill_screen"), SKILL_TAB_ICON, 1, LevelScreen.class, SkillInfoScreen.class, SkillRestrictionScreen.class));

        HudElementRegistry.addLast(LevelzMain.identifierOf("locked_block_info"), (drawContext, tickDelta) -> {
            TooltipUtil.renderTooltip(Minecraft.getInstance(), drawContext);
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            TooltipUtil.renderItemTooltip(Minecraft.getInstance(), stack, lines);
        });

        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outlineRenderState) -> {
            Minecraft client = Minecraft.getInstance();
            if (!ConfigInit.CONFIG.highlightLocked || client.player == null || client.level == null) {
                return true;
            }
            BlockPos pos = outlineRenderState.pos();
            if (((LevelManagerAccess) client.player).getLevelManager().hasRequiredMiningLevel(client.level.getBlockState(pos).getBlock())) {
                return true;
            }
            Vec3 cameraPos = context.levelState().cameraRenderState.pos;
            context.poseStack().pushPose();
            context.poseStack().translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            context.submitNodeCollector().submitShapeOutline(context.poseStack(), outlineRenderState.shape(), RenderTypes.linesTranslucent(), ARGB.colorFromFloat(0.4F, 1.0F, 0.0F, 0.0F),
                    context.gameRenderer().gameRenderState().windowRenderState.appropriateLineWidth, outlineRenderState.isTranslucent());
            context.poseStack().popPose();
            return false;
        });
    }
}
