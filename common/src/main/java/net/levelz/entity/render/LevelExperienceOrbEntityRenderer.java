package net.levelz.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class LevelExperienceOrbEntityRenderer extends EntityRenderer<LevelExperienceOrbEntity, LevelExperienceOrbEntityRenderer.LevelExperienceOrbRenderState> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/experience/experience_orb.png");
    private static final RenderType LAYER = RenderTypes.entityTranslucent(TEXTURE);

    public LevelExperienceOrbEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }

    @Override
    protected int getBlockLightLevel(LevelExperienceOrbEntity experienceOrbEntity, BlockPos blockPos) {
        return Mth.clamp(super.getBlockLightLevel(experienceOrbEntity, blockPos) + 7, 0, 15);
    }

    @Override
    public void submit(LevelExperienceOrbRenderState state, PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        matrixStack.pushPose();
        int j = state.orbSize * 100;
        float h = (float) (j % 4 * 16) / 64.0F;
        float k = (float) (j % 4 * 16 + 16) / 64.0F;
        float l = (float) (j / 4 * 16) / 64.0F;
        float m = (float) (j / 4 * 16 + 16) / 64.0F;
        float r = state.ageInTicks / 2.0F;
        int s = Math.max(80, (int) ((Mth.sin(r + 0.0F) + 1.0F) * 0.5F * 255.0F));
        int u = Math.max(100, (int) ((Mth.sin(r + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F));
        int light = state.lightCoords;
        matrixStack.translate(0.0F, 0.1F, 0.0F);
        matrixStack.rotate(camera.orientation);
        matrixStack.scale(0.3F, 0.3F, 0.3F);
        submitNodeCollector.submitCustomGeometry(matrixStack, LAYER, (entry, vertexConsumer) -> {
            vertex(vertexConsumer, entry, -0.5F, -0.25F, u, s, 255, h, m, light);
            vertex(vertexConsumer, entry, 0.5F, -0.25F, u, s, 255, k, m, light);
            vertex(vertexConsumer, entry, 0.5F, 0.75F, u, s, 255, k, l, light);
            vertex(vertexConsumer, entry, -0.5F, 0.75F, u, s, 255, h, l, light);
        });
        matrixStack.popPose();
        super.submit(state, matrixStack, submitNodeCollector, camera);
    }

    private static void vertex(VertexConsumer vertexConsumer, PoseStack.Pose matrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        vertexConsumer.addVertex(matrix, x, y, 0.0F).setColor(red, green, blue, 128).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(matrix, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public LevelExperienceOrbRenderState createRenderState() {
        return new LevelExperienceOrbRenderState();
    }

    @Override
    public void extractRenderState(LevelExperienceOrbEntity experienceOrbEntity, LevelExperienceOrbRenderState state, float partialTicks) {
        super.extractRenderState(experienceOrbEntity, state, partialTicks);
        state.orbSize = experienceOrbEntity.getOrbSize();
    }

    public static class LevelExperienceOrbRenderState extends EntityRenderState {
        public int orbSize;
    }
}
