package net.levelz.init;

import com.mojang.blaze3d.vertex.PoseStack;
import net.levelz.access.LevelManagerAccess;
import net.levelz.LevelzMain;
import net.levelz.screen.SkillInfoScreen;
import net.levelz.screen.SkillRestrictionScreen;
import net.levelz.screen.LevelScreen;
import net.levelz.screen.widget.LevelzTab;
import net.levelz.screen.widget.VanillaInventoryTab;
import net.levelz.util.TooltipUtil;
import net.libz.registry.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RenderInit {

    public static final Identifier SKILL_TAB_ICON = LevelzMain.identifierOf("textures/gui/sprites/skill_tab_icon.png");
    public static final Identifier BAG_TAB_ICON = LevelzMain.identifierOf("textures/gui/sprites/bag_tab_icon.png");

    public static final Identifier MINEABLE_INFO = LevelzMain.identifierOf("mineable_info");
    public static final Identifier MINEABLE_LEVEL_INFO = LevelzMain.identifierOf("mineable_level_info");
    public static final Identifier LOCKED_BLOCK_INFO = LevelzMain.identifierOf("locked_block_info");

    public static void registerTabs() {
        TabRegistry.registerInventoryTab(new VanillaInventoryTab(Component.translatable("container.crafting"), BAG_TAB_ICON, 0, InventoryScreen.class));
        TabRegistry.registerInventoryTab(new LevelzTab(Component.translatable("screen.levelz.skill_screen"), SKILL_TAB_ICON, 1, LevelScreen.class, SkillInfoScreen.class, SkillRestrictionScreen.class));
    }

    public static void renderLockedBlockInfo(GuiGraphicsExtractor drawContext) {
        TooltipUtil.renderTooltip(Minecraft.getInstance(), drawContext);
    }

    public static void appendItemTooltip(ItemStack stack, List<Component> lines) {
        TooltipUtil.renderItemTooltip(Minecraft.getInstance(), stack, lines);
    }

    public static boolean isLockedOutline(BlockPos pos) {
        Minecraft client = Minecraft.getInstance();
        if (!ConfigInit.CONFIG.highlightLocked || client.player == null || client.level == null) {
            return false;
        }
        return !((LevelManagerAccess) client.player).getLevelManager().hasRequiredMiningLevel(client.level.getBlockState(pos).getBlock());
    }

    public static void renderLockedOutline(BlockOutlineRenderState outlineRenderState, SubmitNodeCollector submitNodeCollector, PoseStack poseStack, LevelRenderState levelRenderState) {
        BlockPos pos = outlineRenderState.pos();
        Vec3 cameraPos = levelRenderState.cameraRenderState.pos;
        poseStack.pushPose();
        poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
        submitNodeCollector.submitShapeOutline(poseStack, outlineRenderState.shape(), RenderTypes.linesTranslucent(), ARGB.colorFromFloat(0.4F, 1.0F, 0.0F, 0.0F),
                Minecraft.getInstance().gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth, outlineRenderState.isTranslucent());
        poseStack.popPose();
    }
}
