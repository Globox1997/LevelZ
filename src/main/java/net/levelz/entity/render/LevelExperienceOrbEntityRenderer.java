package net.levelz.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class LevelExperienceOrbEntityRenderer extends EntityRenderer<LevelExperienceOrbEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/experience_orb.png");
    private static final RenderType LAYER = RenderType.itemEntityTranslucentCull(TEXTURE);

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
    public void render(LevelExperienceOrbEntity experienceOrbEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        int j = experienceOrbEntity.getOrbSize()*100;
        float h = (float)(j % 4 * 16) / 64.0F;
        float k = (float)(j % 4 * 16 + 16) / 64.0F;
        float l = (float)(j / 4 * 16) / 64.0F;
        float m = (float)(j / 4 * 16 + 16) / 64.0F;
        float r = ((float)experienceOrbEntity.tickCount + g) / 2.0F;
        int s = Math.max(80,(int)((Mth.sin(r + 0.0F) + 1.0F) * 0.5F * 255.0F));
        int u = Math.max(100,(int)((Mth.sin(r + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F));
        matrixStack.translate(0.0F, 0.1F, 0.0F);
        matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        matrixStack.scale(0.3F, 0.3F, 0.3F);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        PoseStack.Pose entry = matrixStack.last();
        vertex(vertexConsumer, entry, -0.5F, -0.25F, u,s, 255, h, m, i);
        vertex(vertexConsumer, entry, 0.5F, -0.25F, u,s, 255, k, m, i);
        vertex(vertexConsumer, entry, 0.5F, 0.75F, u,s, 255, k, l, i);
        vertex(vertexConsumer, entry, -0.5F, 0.75F, u,s, 255, h, l, i);
        matrixStack.popPose();
        super.render(experienceOrbEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void vertex(VertexConsumer vertexConsumer, PoseStack.Pose matrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        vertexConsumer.addVertex(matrix, x, y, 0.0F).setColor(red, green, blue, 128).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(matrix, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(LevelExperienceOrbEntity experienceOrbEntity) {
        return TEXTURE;
    }
}
